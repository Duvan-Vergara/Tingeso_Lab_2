import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import reserveService from "../../services/reserve.service";
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
import { add, format } from "date-fns";

const ReserveList = () => {
  const [reserves, setReserves] = useState([]);
  const navigate = useNavigate();

  const formatDate = (isoDate) => {
    return format(new Date(isoDate), "dd-MM-yyyy");
  };

  const init = () => {
    reserveService
      .getAllReserves()
      .then((response) => {
        console.log("Reservas cargadas:", response.data);
        setReserves(response.data);
      })
      .catch((error) => {
        console.error("Error al cargar las reservas:", error);
      });
  };

  useEffect(() => {
    init();
  }, []);


  const handleEdit = (id) => {
    navigate(`/reserve/edit/${id}`);
  };

  const handleDelete = (id) => {
    const confirmDelete = window.confirm(
      "¿Está seguro de que desea eliminar esta reserva?"
    );
    if (confirmDelete) {
      reserveService
        .deleteReserveById(id)
        .then(() => {
          console.log("Reserva eliminada con éxito.");
          init(); // Recargar la lista de reservas
        })
        .catch((error) => {
          console.error("Error al eliminar la reserva:", error);
        });
    }
  };

  const handleAdd = () => {
    navigate("/reserve/add");
  };

  const handleSendPaymentReceipt = (id) => {
    reserveService
      .sendPaymentReceipt(id)
      .then(() => {
        alert("Comprobante de pago enviado con éxito.");
      })
      .catch((error) => {
        console.error("Error al enviar el comprobante de pago:", error);
        alert("Error al enviar el comprobante de pago.");
      });
  };

  const handleSendPaymentReceiptV2 = (id) => {
    reserveService
      .sendPaymentReceiptV2(id)
      .then(() => {
        alert("Comprobante de pago enviado con éxito (versión 2).");
      })
      .catch((error) => {
        console.error("Error al enviar el comprobante de pago (versión 2):", error);
        alert("Error al enviar el comprobante de pago (versión 2).");
      });
  };


  return (
    <TableContainer component={Paper} sx={{ backgroundColor: "rgba(30, 30, 47, 0.9)" }}>
      <h3 style={{ color: "var(--text-optional-color)", textAlign: "center" }}>Lista de Reservas</h3>
      <Button
        variant="contained"
        sx={{
          backgroundColor: "var(--primary-color)",
          color: "var(--text-color)",
          "&:hover": { backgroundColor: "var(--hover-color)" },
        }}
        onClick={handleAdd}
        style={{ margin: "1rem" }}
      >
        Crear Reserva
      </Button>
      <Table sx={{ minWidth: 650 }} size="small" aria-label="a dense table">
        <TableHead>
          <TableRow>
            <TableCell align="left" sx={{ color: "var(--text-color)", fontWeight: "bold" }}>
              Código
            </TableCell>
            <TableCell align="left" sx={{ color: "var(--text-color)", fontWeight: "bold" }}>
              Cliente
            </TableCell>
            <TableCell align="left" sx={{ color: "var(--text-color)", fontWeight: "bold" }}>
              Fecha
            </TableCell>
            <TableCell align="center" sx={{ color: "var(--text-color)", fontWeight: "bold" }}>
              Número de Personas
            </TableCell>
            <TableCell align="center" sx={{ color: "var(--text-color)", fontWeight: "bold" }}>
              Precio Final
            </TableCell>
            <TableCell align="center" sx={{ color: "var(--text-color)", fontWeight: "bold" }}>
              Operaciones
            </TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {reserves.map((reserve) => (
            <TableRow key={reserve.id}>
              <TableCell sx={{ color: "var(--text-color)" }}>{reserve.id}</TableCell>
              <TableCell sx={{ color: "var(--text-color)" }}>
                {reserve.reserves_users.length > 0 ? reserve.reserves_users[0].name : "Sin cliente"}
              </TableCell>
              <TableCell sx={{ color: "var(--text-color)" }}>{formatDate(reserve.reserveday)}</TableCell>
              <TableCell align="center" sx={{ color: "var(--text-color)" }}>{reserve.reserves_users.length}</TableCell>
              <TableCell align="center" sx={{ color: "var(--text-color)" }}>
                {typeof reserve.final_price === "number"
                  ? reserve.final_price.toLocaleString("es-CL", { style: "currency", currency: "CLP" })
                  : "N/A"}
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
                  onClick={() => handleEdit(reserve.id)}
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
                  onClick={() => handleDelete(reserve.id)}
                  style={{ marginLeft: "0.5rem" }}
                  startIcon={<DeleteIcon />}
                >
                  Eliminar
                </Button>
                <Button
                  variant="contained"
                  color="primary"
                  size="small"
                  onClick={() => handleSendPaymentReceipt(reserve.id)}
                  style={{ marginLeft: "0.5rem" }}
                >
                  Enviar Comprobante
                </Button>
                <Button
                  variant="contained"
                  color="secondary"
                  size="small"
                  onClick={() => handleSendPaymentReceiptV2(reserve.id)}
                  style={{ marginLeft: "0.5rem" }}
                >
                  Enviar Comprobante 2
                </Button>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default ReserveList;