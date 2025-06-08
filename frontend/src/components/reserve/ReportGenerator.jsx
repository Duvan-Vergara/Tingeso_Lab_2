import { useState } from "react";
import Button from "@mui/material/Button";
import Menu from "@mui/material/Menu";
import MenuItem from "@mui/material/MenuItem";
import Typography from "@mui/material/Typography";
import reportService from "../../services/report.service";

const ReportGenerator = () => {
  const [startDate, setStartDate] = useState(null);
  const [endDate, setEndDate] = useState(null);
  const [anchorElStart, setAnchorElStart] = useState(null);
  const [anchorElEnd, setAnchorElEnd] = useState(null);

  const months = [
    "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
    "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
  ];

  const years = Array.from({ length: 10 }, (_, i) => new Date().getFullYear() - i);

  const handleGenerateTariffReport = () => {
    if (!startDate || !endDate) {
      alert("Por favor, seleccione las fechas de inicio y fin.");
      return;
    }

    if (new Date(startDate) > new Date(endDate)) {
      alert("La fecha de inicio no puede ser posterior a la fecha de fin.");
      return;
    }

    reportService
      .generateTariffReport(startDate, endDate)
      .then((response) => {
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement("a");
        link.href = url;
        link.setAttribute("download", "reporte_tarifas.xlsx");
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      })
      .catch((error) => {
        console.error("Error al generar el reporte de tarifas:", error);
        alert("Error al generar el reporte de tarifas.");
      });
  };

  const handleGenerateGroupSizeReport = () => {
    if (!startDate || !endDate) {
      alert("Por favor, seleccione las fechas de inicio y fin.");
      return;
    }
    if (new Date(startDate) > new Date(endDate)) {
      alert("La fecha de inicio no puede ser posterior a la fecha de fin.");
      return;
    }
    reportService
      .generateGroupSizeReport(startDate, endDate)
      .then((response) => {
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement("a");
        link.href = url;
        link.setAttribute("download", "reporte_tamanio_grupo.xlsx");
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      })
      .catch((error) => {
        console.error("Error al generar el reporte de tamaño de grupo:", error);
        alert("Error al generar el reporte de tamaño de grupo.");
      });
  };


  const handleOpenStartMenu = (event) => {
    setAnchorElStart(event.currentTarget);
  };

  const handleOpenEndMenu = (event) => {
    setAnchorElEnd(event.currentTarget);
  };

  const handleSelectStartDate = (year, month) => {
    setStartDate(`${year}-${String(month + 1).padStart(2, "0")}-01`);
    setAnchorElStart(null);
  };

  const handleSelectEndDate = (year, month) => {
    setEndDate(`${year}-${String(month + 1).padStart(2, "0")}-01`);
    setAnchorElEnd(null);
  };

  return (
    <div style={{ textAlign: "center", marginTop: "2rem" }}>
      <Typography variant="h5" style={{ marginBottom: "1rem", color: "var(--text-optional-color)" }}>
        Generar Reporte
      </Typography>

      <div style={{ marginBottom: "1rem" }}>
        <Button
          variant="contained"
          sx={{
            backgroundColor: "var(--primary-color)",
            color: "var(--text-color)",
            "&:hover": { backgroundColor: "var(--accent-color)" },
          }}
          onClick={handleOpenStartMenu}
          style={{ marginRight: "1rem" }}
        >
          Seleccionar Fecha de Inicio
        </Button>
        <Menu
          anchorEl={anchorElStart}
          open={Boolean(anchorElStart)}
          onClose={() => setAnchorElStart(null)}
        >
          {years.map((year) => (
            <div key={year}>
              <Typography variant="subtitle1" style={{ padding: "0.5rem" }}>
                {year}
              </Typography>
              {months.map((month, index) => (
                <MenuItem
                  key={`${year}-${month}`}
                  onClick={() => handleSelectStartDate(year, index)}
                >
                  {month}
                </MenuItem>
              ))}
            </div>
          ))}
        </Menu>

        <Button
          variant="contained"
          sx={{
            backgroundColor: "var(--secondary-color)",
            color: "var(--text-color)",
            "&:hover": { backgroundColor: "var(--accent-color)" },
          }}
          onClick={handleOpenEndMenu}
        >
          Seleccionar Fecha de Fin
        </Button>
        <Menu
          anchorEl={anchorElEnd}
          open={Boolean(anchorElEnd)}
          onClose={() => setAnchorElEnd(null)}
        >
          {years.map((year) => (
            <div key={year}>
              <Typography variant="subtitle1" style={{ padding: "0.5rem" }}>
                {year}
              </Typography>
              {months.map((month, index) => (
                <MenuItem
                  key={`${year}-${month}`}
                  onClick={() => handleSelectEndDate(year, index)}
                >
                  {month}
                </MenuItem>
              ))}
            </div>
          ))}
        </Menu>
      </div>

      <Typography variant="body1" style={{ marginBottom: "1rem", color: "var(--text-color)" }}>
        Fecha de Inicio: {startDate || "No seleccionada"} <br />
        Fecha de Fin: {endDate || "No seleccionada"}
      </Typography>

      <div style={{ display: "flex", justifyContent: "center", gap: "1rem" }}>
        <Button
          variant="contained"
          color="secondary"
          onClick={handleGenerateTariffReport}
          disabled={!startDate || !endDate}
        >
          Generar Reporte de Tarifas
        </Button>

        <Button
          variant="contained"
          color="secondary"
          onClick={handleGenerateGroupSizeReport}
          disabled={!startDate || !endDate}
        >
          Generar Reporte de Tamaño de Grupo
        </Button>
      </div>
    </div>
  );
};

export default ReportGenerator;