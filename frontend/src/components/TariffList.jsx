import { useEffect, useState } from "react";
import tariffService from "../services/tariff.service";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import Button from "@mui/material/Button";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import { useNavigate } from "react-router-dom";

const TariffList = () => {
  const [tariffs, setTariffs] = useState([]);
  const navigate = useNavigate();

  // Cargar tarifas desde el backend
  const loadTariffs = () => {
    tariffService
      .getAllTariffs()
      .then((response) => {
        setTariffs(response.data);
      })
      .catch((error) => {
        console.error("Error al cargar las tarifas:", error);
      });
  };

  useEffect(() => {
    loadTariffs();
  }, []);

  // Manejar eliminación de tarifas
  const handleDelete = (id) => {
    const confirmDelete = window.confirm(
      "¿Está seguro de que desea eliminar esta tarifa?"
    );
    if (confirmDelete) {
      tariffService
        .deleteTariffById(id)
        .then(() => {
          console.log("Tarifa eliminada con éxito.");
          loadTariffs(); // Recargar la lista de tarifas
        })
        .catch((error) => {
          console.error("Error al eliminar la tarifa:", error);
        });
    }
  };

  // Navegar a la página de edición
  const handleEdit = (id) => {
    navigate(`/tariff/edit/${id}`);
  };

  // Navegar a la página de agregar
  const handleAdd = () => {
    navigate("/tariff/add");
  };

  return (
    <TableContainer component={Paper} sx={{ backgroundColor: "rgba(30, 30, 47, 0.9)" }}>
      <h3 style={{ color: "var(--text-optional-color)", textAlign: "center" }}>Lista de Tarifas</h3>
      <Button
        variant="contained"
        sx={{
          backgroundColor: "var(--primary-color)",
          color: "var(--text-color)",
          "&:hover": { backgroundColor: "var(--hover-color)" },
        }}
        onClick={() => handleAdd}
        style={{ margin: "1rem" }}
      >
        Añadir Tarifa
      </Button>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell align="left" sx={{ fontWeight: "bold", color: "var(--text-color)" }}>
              Vueltas
            </TableCell>
            <TableCell align="left" sx={{ fontWeight: "bold", color: "var(--text-color)" }}>
              Máx. Minutos
            </TableCell>
            <TableCell align="left" sx={{ fontWeight: "bold", color: "var(--text-color)" }}>
              Precio Regular
            </TableCell>
            <TableCell align="left" sx={{ fontWeight: "bold", color: "var(--text-color)" }}>
              Precio Fin de Semana
            </TableCell>
            <TableCell align="left" sx={{ fontWeight: "bold", color: "var(--text-color)" }}>
              Precio Feriado
            </TableCell>
            <TableCell align="center" sx={{ fontWeight: "bold", color: "var(--text-color)" }}>
              Operaciones
            </TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {tariffs.map((tariff) => (
            <TableRow key={tariff.id}>
              <TableCell align="left" sx={{ color: "var(--text-color)" }}>
                {tariff.laps}
              </TableCell>
              <TableCell align="left" sx={{ color: "var(--text-color)" }}>
                {tariff.maxMinutes}
              </TableCell>
              <TableCell align="left" sx={{ color: "var(--text-color)" }}>
                {tariff.regularPrice.toLocaleString("es-CL")}
              </TableCell>
              <TableCell align="left" sx={{ color: "var(--text-color)" }}>
                {tariff.weekendPrice.toLocaleString("es-CL")}
              </TableCell>
              <TableCell align="left" sx={{ color: "var(--text-color)" }}>
                {tariff.holidayPrice.toLocaleString("es-CL")}
              </TableCell>
              <TableCell align="center">
                <Button
                  variant="contained"
                  sx={{
                    backgroundColor: "var(--primary-color)",
                    color: "var(--text-color)",
                    "&:hover": { backgroundColor: "var(--hover-color)" },
                  }}
                  size="small"
                  onClick={() => handleEdit(tariff.id)}
                  startIcon={<EditIcon />}
                >
                  Editar
                </Button>
                <Button
                  variant="contained"
                  sx={{
                    backgroundColor: "var(--secondary-color)",
                    color: "var(--text-color)",
                    "&:hover": { backgroundColor: "var(--hover-color)" },
                  }}
                  size="small"
                  onClick={() => handleDelete(tariff.id)}
                  style={{ marginLeft: "0.5rem" }}
                  startIcon={<DeleteIcon />}
                >
                  Eliminar
                </Button>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default TariffList;