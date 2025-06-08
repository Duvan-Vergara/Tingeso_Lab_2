import { useState, useEffect } from "react";
import tariffSpecialService from "../../services/tariffspecial.service";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import SaveIcon from "@mui/icons-material/Save";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import CustomTextField from "../CustomTextField";
import FormControl from "@mui/material/FormControl";
import { useNavigate } from "react-router-dom";

const AddEditTariffSpecial = () => {
  const [weekendDiscountPercentage, setWeekendDiscountPercentage] = useState(0);
  const [holidayIncreasePercentage, setHolidayIncreasePercentage] = useState(0);
  const navigate = useNavigate();

  useEffect(() => {
    tariffSpecialService.getTariffSpecial()
      .then((response) => {
        const tariff = response.data;
        if (tariff) {
          setWeekendDiscountPercentage(tariff.weekendDiscountPercentage);
          setHolidayIncreasePercentage(tariff.holidayIncreasePercentage);
        }
      })
      .catch((error) => {
        console.error("Error al cargar la tarifa especial:", error);
      });
  }, []);

  const saveTariffSpecial = (e) => {
    e.preventDefault();
    const tariff = { weekendDiscountPercentage, holidayIncreasePercentage };
    tariffSpecialService.updateTariffSpecial(tariff)
      .then(() => {
        navigate("/tariff/special/list");
      })
      .catch((error) => {
        console.error("Error al actualizar la tarifa especial:", error);
      });
  };

  return (
    <Box
      display="flex"
      flexDirection="column"
      alignItems="center"
      justifyContent="center"
      component="form"
      sx={{
        backgroundColor: "var(--optional-color)",
        padding: "2rem",
        borderRadius: "12px",
        boxShadow: "0 4px 8px rgba(90, 26, 26, 0.5)",
        maxWidth: "600px",
        margin: "2rem auto",
        border: "1px solid var(--secondary-color)",
      }}
      onSubmit={saveTariffSpecial}
    >
      <h3 style={{ color: "var(--text-optional-color)" }}>Editar Tarifa Especial</h3>
      <FormControl fullWidth>
        <CustomTextField
          label="Descuento Fin de Semana (%)"
          type="number"
          value={weekendDiscountPercentage}
          onChange={(e) => setWeekendDiscountPercentage(Number(e.target.value))}
        />
      </FormControl>
      <FormControl fullWidth>
        <CustomTextField
          label="Aumento Festivo (%)"
          type="number"
          value={holidayIncreasePercentage}
          onChange={(e) => setHolidayIncreasePercentage(Number(e.target.value))}
        />
      </FormControl>
      <FormControl>
        <Button
          variant="contained"
          sx={{
            backgroundColor: "var(--primary-color)",
            color: "var(--text-color)",
            "&:hover": { backgroundColor: "var(--accent-color)" },
          }}
          type="submit"
          startIcon={<SaveIcon />}
          style={{ marginBottom: "0.5rem" }}
        >
          Guardar
        </Button>
        <Button
          variant="contained"
          sx={{
            backgroundColor: "var(--secondary-color)",
            color: "var(--text-color)",
            "&:hover": { backgroundColor: "var(--accent-color)" },
          }}
          onClick={() => navigate("/tariff/special/list")}
          startIcon={<ArrowBackIcon />}
        >
          Volver
        </Button>
      </FormControl>
    </Box>
  );
};

export default AddEditTariffSpecial;