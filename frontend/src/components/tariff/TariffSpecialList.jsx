import { useEffect, useState } from "react";
import tariffSpecialService from "../../services/tariffspecial.service";
import Button from "@mui/material/Button";
import EditIcon from "@mui/icons-material/Edit";
import { useNavigate } from "react-router-dom";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";

const TariffSpecialList = () => {
  const [tariff, setTariff] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    tariffSpecialService.getTariffSpecial()
      .then((response) => {
        setTariff(response.data);
      })
      .catch((error) => {
        console.error("Error al cargar la tarifa especial:", error);
      });
  }, []);

  const handleEdit = () => {
    navigate("/tariff/special/edit");
  };

  return (
    <TableContainer component={Paper} sx={{ backgroundColor: "rgba(30, 30, 47, 0.9)" }}>
      <h3 style={{ color: "var(--text-optional-color)", textAlign: "center" }}>Tarifa Especial</h3>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell sx={{ color: "var(--text-color)", fontWeight: "bold" }}>Descuento Fin de Semana (%)</TableCell>
            <TableCell sx={{ color: "var(--text-color)", fontWeight: "bold" }}>Aumento Festivo (%)</TableCell>
            <TableCell align="center" sx={{ color: "var(--text-color)", fontWeight: "bold" }}>Operaciones</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {tariff && (
            <TableRow>
              <TableCell sx={{ color: "var(--text-color)" }}>{tariff.weekendDiscountPercentage}</TableCell>
              <TableCell sx={{ color: "var(--text-color)" }}>{tariff.holidayIncreasePercentage}</TableCell>
              <TableCell align="center">
                <Button
                  variant="contained"
                  sx={{
                    backgroundColor: "var(--primary-color)",
                    color: "var(--text-color)",
                    "&:hover": { backgroundColor: "var(--hover-color)" },
                  }}
                  size="small"
                  onClick={handleEdit}
                  startIcon={<EditIcon />}
                >
                  Editar
                </Button>
              </TableCell>
            </TableRow>
          )}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default TariffSpecialList;