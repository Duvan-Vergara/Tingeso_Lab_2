package edu.mtisw.backend.services;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import edu.mtisw.backend.entities.ReserveEntity;
import edu.mtisw.backend.entities.TariffEntity;
import edu.mtisw.backend.entities.UserEntity;
import edu.mtisw.backend.repositories.ReserveRepository;
import edu.mtisw.backend.repositories.TariffRepository;
import edu.mtisw.backend.utils.ComplementReserve;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.stream.IntStream;
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
public class ReserveService {

    @Autowired
    ReserveRepository reserveRepository;

    @Autowired
    TariffRepository tariffRepository;

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    ComplementReserve complementReserve;


    @Value("${spring.mail.username}")
    private String senderEmail;

    public List<ReserveEntity> getReserves() { return new ArrayList<>(reserveRepository.findAll()); }

    public ReserveEntity saveReserve(ReserveEntity reserve) {
        // Calcular la tarifa si no está especificada
        if (reserve.getTariff() == null) {
            // Obtener las tarifas disponibles
            List<TariffEntity> availableTariffs = tariffRepository.findAll();

            TariffEntity calculatedTariff = calculateTariffForReserve(reserve.getBegin(), reserve.getFinish(), availableTariffs);
            reserve.setTariff(calculatedTariff);
            // Ajustar la hora de finalización según la tarifa calculada
            reserve.setFinish(reserve.getBegin().plusMinutes(calculatedTariff.getMaxMinutes()));
        }
        // Guardar la reserva
        return reserveRepository.save(reserve);
    }

    public ReserveEntity getReserveById(Long id){
        return reserveRepository.findById(id).get();
    }

    public List<ReserveEntity> getReserveByDay(int day) { return reserveRepository.getReserveByDate_Day(day); }

    public List<ReserveEntity> getReserveByMonth(int month) { return reserveRepository.getReserveByDate_Month(month); }

    public List<List<String>> getReserveByWeek(int year, int month, int day) {
        LocalDate date = LocalDate.of(year, month, day);
        LocalDate startDate = date.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate endDate = startDate.plusDays(6);

        // Obtener reservas entre las fechas
        List<ReserveEntity> reserves = reserveRepository.getReserveByDate_DateBetween(startDate, endDate);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm"); // Formato sin ceros a la izquierda

        // Agrupar reservas por día de la semana y formatear la información
        return IntStream.range(0, 7)
                .mapToObj(i -> startDate.plusDays(i))
                .map(d -> reserves.stream()
                        .filter(r -> r.getDate().equals(d))
                        .map(r -> {
                            UserEntity user = r.getGroup().iterator().next(); // Obtener el primer usuario
                            String startTime = r.getBegin().format(timeFormatter);
                            String endTime = r.getFinish().format(timeFormatter);
                            return user.getName() + " (" + startTime + " - " + endTime + ")";
                        })
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    public List<ReserveEntity> getReservesByDate_MonthANDRut(String rut, int month) {
        return reserveRepository.getReservesByDateMonthAndRut(rut, month);
    }

    public boolean deleteReserveById(Long id) throws Exception {
        try {
            reserveRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public double getTariffForDate(ReserveEntity reserve) {
        LocalDate reserveDate = reserve.getDate();
        if (complementReserve.isSpecialDay(reserveDate)) {
            return reserve.getTariff().getHolidayPrice();
        } else if (complementReserve.isWeekend(reserveDate)) {
            return reserve.getTariff().getWeekendPrice();
        } else {
            return reserve.getTariff().getRegularPrice();
        }
    }

    public TariffEntity calculateTariffForReserve(LocalTime startTime, LocalTime endTime, List<TariffEntity> availableTariffs) {
        if (availableTariffs == null || availableTariffs.isEmpty()) {
            throw new IllegalArgumentException("No hay tarifas disponibles para calcular.");
        }

        // Calcular la duración en minutos
        long durationInMinutes = java.time.Duration.between(startTime, endTime).toMinutes();


        // Inicializar las tarifas mínima y máxima
        TariffEntity shortestTariff = null;
        TariffEntity longestTariff = null;

        // Buscar la tarifa adecuada en una sola pasada
        for (TariffEntity tariff : availableTariffs) {
            if (shortestTariff == null || tariff.getTotalDuration() < shortestTariff.getTotalDuration()) {
                shortestTariff = tariff;
            }
            if (longestTariff == null || tariff.getTotalDuration() > longestTariff.getTotalDuration()) {
                longestTariff = tariff;
            }
            if (durationInMinutes <= tariff.getTotalDuration()) {
                return tariff; // Retornar la primera tarifa adecuada
            }
        }
        // Si la duración es mayor que la tarifa más larga, retornar la tarifa máxima
        return longestTariff;
    }

    public double calculateFinalPrice(ReserveEntity reserve, int month) {
        double totalPrice = 0;
        int birthdayLimit = complementReserve.calculateBirthdayLimit(reserve.getGroup().size());
        double basePrice = getTariffForDate(reserve);

        for (UserEntity user : reserve.getGroup()) {
            List<ReserveEntity> userReserves = reserveRepository.getReservesByDateMonthAndRut(user.getRut(), month);

            double bestDiscount = complementReserve.calculateBestDiscount(reserve, userReserves);

            // Descuento por cumpleaños
            if (complementReserve.isBirthday(user, reserve.getDate()) && birthdayLimit > 0) {
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

        // Crear encabezados
        Row headerRow = sheet.createRow(0);
        String[] headers = {
                "Código de Reserva", "Fecha y Hora de Reserva", "Número de Vueltas/Max Tiempo",
                "Cantidad de Personas", "Nombre de la Persona que Reservó", "", ""
        };
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        // Llenar información de la reserva
        Row infoRow = sheet.createRow(1);
        infoRow.createCell(0).setCellValue(reserve.getId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");
        LocalDateTime dateTime = LocalDateTime.of(reserve.getDate(), reserve.getBegin());
        String formattedDateTime = dateTime.format(formatter);
        infoRow.createCell(1).setCellValue(formattedDateTime);
        infoRow.createCell(2).setCellValue(reserve.getTariff().getLaps() + " vueltas / " + reserve.getTariff().getMaxMinutes() + " minutos");
        infoRow.createCell(3).setCellValue(reserve.getGroup().size());
        infoRow.createCell(4).setCellValue(reserve.getGroup().iterator().next().getName());
        for (int i = 5; i < 7; i++) {
            infoRow.createCell(i); // Crear celdas vacías
        }

        // Crear encabezados para el detalle de pago
        Row paymentHeaderRow = sheet.createRow(3);
        String[] paymentHeaders = {
                "Nombre de Cliente", "Tarifa Base", "Descuento (%)",
                "Descuento especial (%)", "Monto Final", "IVA", "Monto Total"
        };
        for (int i = 0; i < paymentHeaders.length; i++) {
            paymentHeaderRow.createCell(i).setCellValue(paymentHeaders[i]);
        }

        // Llenar detalle de pago
        int rowNum = 4;
        double totalAmount = 0;
        double iva = 0;
        for (UserEntity user : reserve.getGroup()) {
            Row row = sheet.createRow(rowNum++);
            double basePrice = reserve.getTariff().getRegularPrice();
            double groupDiscount = complementReserve.calculateGroupSizeDiscount(reserve.getGroup().size());
            List<ReserveEntity> userReserves = reserveRepository.getReservesByDateMonthAndRut(user.getRut(), reserve.getDate().getMonthValue());
            double frequentDiscount = complementReserve.calculateFrequentCustomerDiscount(userReserves);
            double bestDiscount = Math.max(groupDiscount, frequentDiscount);
            double finalAmount = basePrice * (1 - bestDiscount);
            double ivaAmount = finalAmount * 0.19;
            double totalWithIva = finalAmount + ivaAmount;

            row.createCell(0).setCellValue(user.getName());
            row.createCell(1).setCellValue(basePrice);
            row.createCell(2).setCellValue(groupDiscount * 100);
            row.createCell(3).setCellValue(frequentDiscount * 100);
            row.createCell(4).setCellValue(finalAmount);
            row.createCell(5).setCellValue(ivaAmount);
            row.createCell(6).setCellValue(totalWithIva);

            totalAmount += finalAmount;
            iva += ivaAmount;
        }

        // Agregar fila para el precio total de la reserva
        Row totalReserveRow = sheet.createRow(rowNum);
        for (int i = 0; i < 4; i++) {
            totalReserveRow.createCell(i); // Crear celdas vacías
        }
        totalReserveRow.createCell(4).setCellValue("Totales:");
        totalReserveRow.createCell(5).setCellValue(iva);
        totalReserveRow.createCell(6).setCellValue(totalAmount + iva);


        // Escribir el archivo Excel a un ByteArrayOutputStream para retornarlo
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

        for (UserEntity user : reserve.getGroup()) {
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

        for (UserEntity user : reserve.getGroup()) {
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

    public JavaMailSender createJavaMailSender() {
        return javaMailSender;
    }

    private List<YearMonth> getMonthsBetween(LocalDate startDate, LocalDate endDate) {
        List<YearMonth> months = new ArrayList<>();
        YearMonth start = YearMonth.from(startDate);
        YearMonth end = YearMonth.from(endDate);

        while (!start.isAfter(end)) {
            months.add(start);
            start = start.plusMonths(1);
        }
        return months;
    }

    private String formatMonth(YearMonth month) {
        return month.format(DateTimeFormatter.ofPattern("MMMM", new Locale("es", "ES"))).toUpperCase();
    }

    private double calculateIncome(List<ReserveEntity> reserves, TariffEntity tariff, YearMonth month) {
        return reserves.stream()
                .filter(r -> {
                    YearMonth reserveMonth = YearMonth.from(r.getDate());
                    return reserveMonth.equals(month) &&
                            tariff.getId().equals(r.getTariff().getId());
                })
                .mapToDouble(ReserveEntity::getFinalPrice)
                .sum();
    }

    private double calculateGroupSizeIncome(List<ReserveEntity> reserves, int minSize, int maxSize, YearMonth month) {
        return reserves.stream()
                .filter(r -> {
                    // Convertir java.sql.Date a LocalDate directamente
                    YearMonth reserveMonth = YearMonth.from(r.getDate());
                    int groupSize = r.getGroup().size();
                    return reserveMonth.equals(month) &&
                            groupSize >= minSize && groupSize <= maxSize;
                })
                .mapToDouble(ReserveEntity::getFinalPrice)
                .sum();
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createMoneyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }


    public byte[] generateTariffReport(LocalDate startDate, LocalDate endDate) throws IOException {

        // Agregar esta validación al inicio del metodo
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha fin.");
        }

        List<TariffEntity> tariffs = tariffRepository.findAll();
        if (tariffs.isEmpty()) {
            throw new IllegalArgumentException("No existen tarifas registradas");
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Reporte por Tarifas");
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle moneyStyle = createMoneyStyle(workbook);

            // Crear encabezados
            Row headerRow = sheet.createRow(0);
            Cell headerCell = headerRow.createCell(0);
            headerCell.setCellValue("Número de vueltas o tiempo máximo permitido");
            headerCell.setCellStyle(headerStyle);

            // Obtener los meses entre las fechas
            List<YearMonth> months = getMonthsBetween(startDate, endDate);

            // Crear encabezados de meses
            for (int i = 0; i < months.size(); i++) {
                Cell monthCell = headerRow.createCell(i + 1);
                monthCell.setCellValue(formatMonth(months.get(i)));
                monthCell.setCellStyle(headerStyle);
            }

            // Columna de total
            Cell totalHeaderCell = headerRow.createCell(months.size() + 1);
            totalHeaderCell.setCellValue("TOTAL");
            totalHeaderCell.setCellStyle(headerStyle);

            // Obtener todas las reservas entre las fechas
            List<ReserveEntity> allReserves = reserveRepository.getReserveByDate_DateBetween(
                    startDate, endDate.plusDays(1));

            // Procesar datos para cada tarifa
            int rowIndex = 1;
            double[] columnTotals = new double[months.size() + 1]; // +1 para el total general

            for (TariffEntity tariff : tariffs) {
                Row dataRow = sheet.createRow(rowIndex++);
                dataRow.createCell(0).setCellValue(
                        tariff.getLaps() + " vueltas o máx " + tariff.getMaxMinutes() + " min");

                double rowTotal = 0;

                // Calcular ingresos por mes para esta tarifa
                for (int i = 0; i < months.size(); i++) {
                    YearMonth month = months.get(i);
                    double monthlyIncome = calculateIncome(allReserves, tariff, month);

                    Cell valueCell = dataRow.createCell(i + 1);
                    valueCell.setCellValue(monthlyIncome);
                    valueCell.setCellStyle(moneyStyle);

                    rowTotal += monthlyIncome;
                    columnTotals[i] += monthlyIncome;
                }

                // Total por tarifa
                Cell rowTotalCell = dataRow.createCell(months.size() + 1);
                rowTotalCell.setCellValue(rowTotal);
                rowTotalCell.setCellStyle(moneyStyle);
                columnTotals[months.size()] += rowTotal;
            }

            // Fila de totales
            Row totalRow = sheet.createRow(rowIndex);
            Cell totalLabelCell = totalRow.createCell(0);
            totalLabelCell.setCellValue("TOTAL");
            totalLabelCell.setCellStyle(headerStyle);

            for (int i = 0; i <= months.size(); i++) {
                Cell totalCell = totalRow.createCell(i + 1);
                totalCell.setCellValue(columnTotals[i]);
                totalCell.setCellStyle(moneyStyle);
            }

            // Ajustar anchos de columna
            for (int i = 0; i <= months.size() + 1; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();
        }
    }

    public byte[] generateGroupSizeReport(LocalDate startDate, LocalDate endDate) throws IOException {
        // Definir las categorías de tamaño de grupo
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha fin.");
        }

        int[][] groupSizeCategories = {{1, 2}, {3, 5}, {6, 10}, {11, 15}};
        String[] categoryLabels = {"1-2 personas", "3-5 personas", "6-10 personas", "11-15 personas"};

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Reporte por Tamaño de Grupo");
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle moneyStyle = createMoneyStyle(workbook);

            // Crear encabezados
            Row headerRow = sheet.createRow(0);
            Cell headerCell = headerRow.createCell(0);
            headerCell.setCellValue("Número de personas");
            headerCell.setCellStyle(headerStyle);

            // Obtener los meses entre las fechas
            List<YearMonth> months = getMonthsBetween(startDate, endDate);

            // Crear encabezados de meses
            for (int i = 0; i < months.size(); i++) {
                Cell monthCell = headerRow.createCell(i + 1);
                monthCell.setCellValue(formatMonth(months.get(i)));
                monthCell.setCellStyle(headerStyle);
            }

            // Columna de total
            Cell totalHeaderCell = headerRow.createCell(months.size() + 1);
            totalHeaderCell.setCellValue("TOTAL");
            totalHeaderCell.setCellStyle(headerStyle);

            // Obtener todas las reservas entre las fechas
            List<ReserveEntity> allReserves = reserveRepository.getReserveByDate_DateBetween(
                    startDate, endDate.plusDays(1));

            // Procesar datos para cada categoría de tamaño
            int rowIndex = 1;
            double[] columnTotals = new double[months.size() + 1]; // +1 para el total general

            for (int i = 0; i < groupSizeCategories.length; i++) {
                int[] range = groupSizeCategories[i];
                String label = categoryLabels[i];

                Row dataRow = sheet.createRow(rowIndex++);
                dataRow.createCell(0).setCellValue(label);

                double rowTotal = 0;

                // Calcular ingresos por mes para esta categoría de tamaño
                for (int j = 0; j < months.size(); j++) {
                    YearMonth month = months.get(j);
                    double monthlyIncome = calculateGroupSizeIncome(allReserves, range[0], range[1], month);

                    Cell valueCell = dataRow.createCell(j + 1);
                    valueCell.setCellValue(monthlyIncome);
                    valueCell.setCellStyle(moneyStyle);

                    rowTotal += monthlyIncome;
                    columnTotals[j] += monthlyIncome;
                }

                // Total por categoría
                Cell rowTotalCell = dataRow.createCell(months.size() + 1);
                rowTotalCell.setCellValue(rowTotal);
                rowTotalCell.setCellStyle(moneyStyle);
                columnTotals[months.size()] += rowTotal;
            }

            // Fila de totales
            Row totalRow = sheet.createRow(rowIndex);
            Cell totalLabelCell = totalRow.createCell(0);
            totalLabelCell.setCellValue("TOTAL");
            totalLabelCell.setCellStyle(headerStyle);

            for (int i = 0; i <= months.size(); i++) {
                Cell totalCell = totalRow.createCell(i + 1);
                totalCell.setCellValue(columnTotals[i]);
                totalCell.setCellStyle(moneyStyle);
            }

            // Ajustar anchos de columna
            for (int i = 0; i <= months.size() + 1; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();
        }
    }
}