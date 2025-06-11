package microservice5.backend.services;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import microservice5.backend.dto.*;
import microservice5.backend.entities.ReserveEntity;
import microservice5.backend.entities.UserEntity;
import microservice5.backend.repositories.DescPersonaClient;
import microservice5.backend.repositories.DesctPersonaFrect;
import microservice5.backend.repositories.ReserveRepository;
import microservice5.backend.repositories.TariffClient;
import microservice5.backend.utils.ComplementReserve;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import com.itextpdf.text.Document;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class ReserveService {

    private final ReserveRepository reserveRepository;

    private final TariffClient tariffClient;

    private final DescPersonaClient descPersonaClient;

    private final DesctPersonaFrect desctPersonaFrect;

    JavaMailSender javaMailSender;

    private final ComplementReserve complementReserve;


    @Value("${spring.mail.username}")
    private String senderEmail;

    public List<ReserveEntity> getReserves() {
        return new ArrayList<>(reserveRepository.findAll());
    }

    public ReserveEntity saveReserve(ReserveEntity reserve) {
        if (reserve.getTariff_id() == null) {
            // llamar a microservicio 1
            long durationInMinutes = java.time.Duration.between(reserve.getBegin(), reserve.getFinish()).toMinutes();
            TiempoDTO tiempoDTO = new TiempoDTO(durationInMinutes);
            MaxMinutesIdDTO calculatedTariff = tariffClient.getBestTariffId(tiempoDTO);
            //definir datos
            reserve.setTariff_id(calculatedTariff.getId());
            reserve.setFinish(reserve.getBegin().plusMinutes(calculatedTariff.getMaxMinutes()));
            reserve.setFinal_price(calculateFinalPrice(reserve, reserve.getReserveday().getMonthValue()));
        }
        // Guardar la reserva
        return reserveRepository.save(reserve);
    }

    public ReserveEntity getReserveById(Long id) {
        return reserveRepository.findById(id).get();
    }

    public List<ReserveDTO> getReserveByWeek(int year, int month, int day) {
        LocalDate date = LocalDate.of(year, month, day);
        LocalDate startDate = date.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate endDate = startDate.plusDays(6);

        // Obtener reservas entre las fechas
        List<ReserveEntity> reserves = reserveRepository.getReserveByDate_DateBetween(startDate, endDate);

        // Mapear a DTO
        return reserves.stream()
                .map(r -> {
                    String username = r.getReserves_users().isEmpty() ? "" : r.getReserves_users().iterator().next().getName();
                    return new ReserveDTO(r.getReserveday(), r.getBegin(), r.getFinish(), username);
                })
                .collect(Collectors.toList());
    }

    public List<ReserveBasicDTO> getReservesByDateBetween(LocalDate startDate, LocalDate endDate) {
        List<ReserveEntity> reserves = reserveRepository.getReserveByDate_DateBetween(startDate, endDate);
        return reserves.stream()
                .map(r -> new ReserveBasicDTO(
                        r.getReserveday(),
                        r.getTariff_id(),
                        r.getFinal_price(),
                        r.getReserves_users().size()
                ))
                .collect(Collectors.toUnmodifiableList());
    }

    public boolean deleteReserveById(Long id) throws Exception {
        try {
            reserveRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public double calculateFinalPrice(ReserveEntity reserve, int month) {
        double totalPrice = 0;
        int birthdayLimit = complementReserve.calculateBirthdayLimit(reserve.getReserves_users().size());

        //aca obtengo el precio base la tarifa, llamando a microservicio 1
        FechaDTO fechaDTO = new FechaDTO(reserve.getReserveday(), reserve.getTariff_id());
        double basePrice = tariffClient.getBasePrice(fechaDTO);
        double bestDiscount = descPersonaClient.obtenerDescuento(new PersonaDTO(reserve.getReserves_users().size())).getBody();

        // Calcular el descuento por grupo
        for (UserEntity user : reserve.getReserves_users()) {
            List<ReserveEntity> userReserves = reserveRepository.getReservesByDateMonthAndRut(user.getRut(), month);

            double userFrectDiscount = desctPersonaFrect.obtenerDescuento(new VecesDTO(userReserves.size())).getBody();
            if (bestDiscount < userFrectDiscount) {
                bestDiscount = userFrectDiscount;
            }
            // Descuento por cumpleaños
            if (complementReserve.isBirthday(user, reserve.getReserveday()) && birthdayLimit > 0) {
                bestDiscount = Math.max(bestDiscount, 0.50);
                birthdayLimit--;
            }
            // Aplicar el descuento al precio base por usuario
            totalPrice += basePrice * (1 - bestDiscount);
        }
        return totalPrice;
    }

    public byte[] generatePaymentReceipt(ReserveEntity reserve) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Comprobante de Pago");

        // Encabezados generales
        Row headerRow = sheet.createRow(0);
        String[] headers = {
                "Código de Reserva", "Fecha y Hora de Reserva", "Número de Vueltas/Max Tiempo",
                "Cantidad de Personas", "Nombre de la Persona que Reservó", "", ""
        };
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        // Obtener datos de la tarifa usando el ID
        TariffBasicDTO tariff = tariffClient.getTariffById(new IdDTO(reserve.getTariff_id())).getBody();
        double basePrice = tariffClient.getBasePrice(new FechaDTO(reserve.getReserveday(), reserve.getTariff_id()));

        // Información de la reserva
        Row infoRow = sheet.createRow(1);
        infoRow.createCell(0).setCellValue(reserve.getId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");
        LocalDateTime dateTime = LocalDateTime.of(reserve.getReserveday(), reserve.getBegin());
        infoRow.createCell(1).setCellValue(dateTime.format(formatter));
        infoRow.createCell(2).setCellValue(tariff.getLaps() + " vueltas / " + tariff.getMax_minutes() + " minutos");
        infoRow.createCell(3).setCellValue(reserve.getReserves_users().size());
        infoRow.createCell(4).setCellValue(reserve.getReserves_users().iterator().next().getName());
        for (int i = 5; i < 7; i++) infoRow.createCell(i);

        // Encabezados detalle de pago
        Row paymentHeaderRow = sheet.createRow(3);
        String[] paymentHeaders = {
                "Nombre de Cliente", "Tarifa Base", "Descuento por Grupo (%)",
                "Descuento por Frecuencia (%)", "Descuento por Cumpleaños (%)", "Descuento Aplicado (%)",
                "Monto Final", "IVA", "Monto Total"
        };
        for (int i = 0; i < paymentHeaders.length; i++) {
            paymentHeaderRow.createCell(i).setCellValue(paymentHeaders[i]);
        }

        // Detalle de pago por usuario
        int rowNum = 4;
        double totalAmount = 0;
        double iva = 0;
        int birthdayLimit = complementReserve.calculateBirthdayLimit(reserve.getReserves_users().size());

        for (UserEntity user : reserve.getReserves_users()) {
            Row row = sheet.createRow(rowNum++);
            // Descuentos desde microservicios
            double groupDiscount = descPersonaClient.obtenerDescuento(new PersonaDTO(reserve.getReserves_users().size())).getBody();
            List<ReserveEntity> userReserves = reserveRepository.getReservesByDateMonthAndRut(user.getRut(), reserve.getReserveday().getMonthValue());
            double frequentDiscount = desctPersonaFrect.obtenerDescuento(new VecesDTO(userReserves.size())).getBody();
            double birthdayDiscount = (complementReserve.isBirthday(user, reserve.getReserveday()) && birthdayLimit > 0) ? 0.50 : 0.0;
            if (birthdayDiscount > 0) birthdayLimit--;

            // Selección del mejor descuento
            double bestDiscount = Math.max(groupDiscount, frequentDiscount);
            bestDiscount = Math.max(bestDiscount, birthdayDiscount);

            double finalAmount = basePrice * (1 - bestDiscount);
            double ivaAmount = finalAmount * 0.19;
            double totalWithIva = finalAmount + ivaAmount;

            row.createCell(0).setCellValue(user.getName());
            row.createCell(1).setCellValue(basePrice);
            row.createCell(2).setCellValue(groupDiscount * 100);
            row.createCell(3).setCellValue(frequentDiscount * 100);
            row.createCell(4).setCellValue(birthdayDiscount * 100);
            row.createCell(5).setCellValue(bestDiscount * 100);
            row.createCell(6).setCellValue(finalAmount);
            row.createCell(7).setCellValue(ivaAmount);
            row.createCell(8).setCellValue(totalWithIva);

            totalAmount += finalAmount;
            iva += ivaAmount;
        }

        // Totales
        Row totalReserveRow = sheet.createRow(rowNum);
        for (int i = 0; i < 6; i++) totalReserveRow.createCell(i);
        totalReserveRow.createCell(6).setCellValue("Totales:");
        totalReserveRow.createCell(7).setCellValue(iva);
        totalReserveRow.createCell(8).setCellValue(totalAmount + iva);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();
        return bos.toByteArray();
    }

    public byte[] convertExcelToPdf(byte[] excelData) throws IOException, DocumentException {
        ByteArrayInputStream bis = new ByteArrayInputStream(excelData);
        Workbook workbook = WorkbookFactory.create(bis);

        Document document = new Document();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, bos);
        document.open();

        Sheet sheet = workbook.getSheetAt(0);

        // Crear una tabla en el PDF con el número de columnas del Excel
        int numberOfColumns = sheet.getRow(0).getLastCellNum();
        com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(numberOfColumns);
        table.setWidthPercentage(100); // Ajustar al ancho de la página

        // Establecer anchos relativos para las columnas
        float[] columnWidths = new float[numberOfColumns];
        Arrays.fill(columnWidths, 1f); // Asignar ancho uniforme a todas las columnas
        table.setWidths(columnWidths);

        // Agregar encabezados de la tabla
        Row headerRow = sheet.getRow(0);
        for (Cell cell : headerRow) {
            table.addCell(new com.itextpdf.text.Phrase(cell.toString()));
        }

        // Agregar datos de las filas
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                // Determinar el número de columnas dinámicamente para cada fila
                int dynamicNumberOfColumns = row.getLastCellNum();
                for (int j = 0; j < dynamicNumberOfColumns; j++) {
                    Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    // Formatear valores numéricos para mayor claridad
                    if (cell.getCellType() == CellType.NUMERIC) {
                        table.addCell(new com.itextpdf.text.Phrase(String.format("%.2f", cell.getNumericCellValue())));
                    } else {
                        table.addCell(new com.itextpdf.text.Phrase(cell.toString()));
                    }
                }
            }
        }
        // Agregar la tabla al documento PDF
        document.add(table);
        document.close();
        workbook.close();
        return bos.toByteArray();
    }

    public void sendEmailWithAttachment(String to, String subject, String text, byte[] attachmentData, String attachmentName) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            message.setSubject(subject);
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(senderEmail);
            helper.setTo(to);
            helper.setText(text);
            helper.addAttachment(attachmentName, new ByteArrayDataSource(attachmentData, "application/pdf"));
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendPaymentReceipts(ReserveEntity reserve) throws IOException, DocumentException {
        byte[] excelData = generatePaymentReceipt(reserve);
        byte[] pdfData = convertExcelToPdf(excelData);

        for (UserEntity user : reserve.getReserves_users()) {
            sendEmailWithAttachment(
                    user.getEmail(),
                    "Comprobante de Pago",
                    "Adjunto encontrará el comprobante de pago de su reserva.",
                    pdfData,
                    "Comprobante_de_Pago.pdf"
            );
        }
    }

    public void sendPaymentReceipts_2(ReserveEntity reserve) throws IOException, DocumentException {
        byte[] excelData = generatePaymentReceipt(reserve);
        byte[] pdfData = convertExcelToPdf(excelData);

        // Crear un pool de hilos para enviar correos en paralelo
        ExecutorService executorService = Executors.newFixedThreadPool(5); // Ajusta el tamaño del pool según tus necesidades

        for (UserEntity user : reserve.getReserves_users()) {
            executorService.submit(() -> {
                try {
                    sendEmailWithAttachment(
                            user.getEmail(),
                            "Comprobante de Pago",
                            "Adjunto encontrará el comprobante de pago de su reserva.",
                            pdfData,
                            "Comprobante_de_Pago.pdf"
                    );
                } catch (Exception e) {
                    e.printStackTrace(); // Manejar errores de envío
                }
            });
        }
        // Cerrar el pool de hilos
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                System.err.println("Algunas tareas no se completaron a tiempo.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
