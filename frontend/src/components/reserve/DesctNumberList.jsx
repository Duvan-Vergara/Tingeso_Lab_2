import { useEffect, useState } from "react";
import desctNumberService from "../../services/desctnumber.service";
import Button from "@mui/material/Button";
import EditIcon from "@mui/icons-material/Edit";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import { useNavigate } from "react-router-dom";

const DesctNumberList = () => {
  const [descts, setDescts] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    desctNumberService.getAllDesctNumbers()
      .then((response) => setDescts(response.data))
      .catch((error) => console.error("Error al cargar descuentos:", error));
  }, []);

  const handleEdit = (desct) => {
    navigate("/desctnumber/edit", { state: desct });
  };

  const handleAdd = () => {
    navigate("/desctnumber/add");
  };

  return (
    <TableContainer component={Paper} sx={{ backgroundColor: "rgba(30, 30, 47, 0.9)" }}>
      <h3 style={{ color: "var(--text-optional-color)", textAlign: "center" }}>Descuentos por Número de Personas</h3>
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
        Añadir Descuento
      </Button>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell sx={{ color: "var(--text-color)", fontWeight: "bold" }}>Mín. Personas</TableCell>
            <TableCell sx={{ color: "var(--text-color)", fontWeight: "bold" }}>Máx. Personas</TableCell>
            <TableCell sx={{ color: "var(--text-color)", fontWeight: "bold" }}>Porcentaje Descuento (%)</TableCell>
            <TableCell align="center" sx={{ color: "var(--text-color)", fontWeight: "bold" }}>Operaciones</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {descts.map((desct) => (
            <TableRow key={desct.id}>
              <TableCell sx={{ color: "var(--text-color)" }}>{desct.minpersonas}</TableCell>
              <TableCell sx={{ color: "var(--text-color)" }}>{desct.maxpersonas}</TableCell>
              <TableCell sx={{ color: "var(--text-color)" }}>{desct.porcentajedesct}</TableCell>
              <TableCell align="center">
                <Button
                  variant="contained"
                  sx={{
                    backgroundColor: "var(--primary-color)",
                    color: "var(--text-color)",
                    "&:hover": { backgroundColor: "var(--hover-color)" },
                  }}
                  size="small"
                  onClick={() => handleEdit(desct)}
                  startIcon={<EditIcon />}
                >
                  Editar
                </Button>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default DesctNumberList;