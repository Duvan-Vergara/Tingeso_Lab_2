import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import tariffService from "../../services/tariff.service";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import SaveIcon from "@mui/icons-material/Save";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import CustomTextField from "../CustomTextField";
import FormControl from "@mui/material/FormControl";

const AddEditTariff = () => {
  const [laps, setLaps] = useState(0);
  const [max_minutes, setMax_minutes] = useState(0);
  const [regular_price, setRegular_price] = useState(0.0);
  const [total_duration, setTotal_duration] = useState(0);
  const { id } = useParams();
  const navigate = useNavigate();

  useEffect(() => {
    if (id) {
      tariffService
        .getTariffById(id)
        .then((response) => {
          const tariff = response.data;
          setLaps(tariff.laps);
          setMax_minutes(tariff.max_minutes);
          setRegular_price(tariff.regular_price);
          setTotal_duration(tariff.total_duration);
        })
        .catch((error) => {
          console.error("Error al cargar la tarifa:", error);
        });
    }
  }, [id]);

  const saveTariff = (e) => {
    e.preventDefault();
    const tariff = { laps, max_minutes, regular_price, total_duration};
    if (id) {
      tariffService
        .saveTariff({ ...tariff, id })
        .then(() => {
          navigate("/tariff/list");
        })
        .catch((error) => {
          console.error("Error al actualizar la tarifa:", error);
        });
    } else {
      tariffService
        .saveTariff(tariff)
        .then(() => {
          navigate("/tariff/list");
        })
        .catch((error) => {
          console.error("Error al crear la tarifa:", error);
        });
    }
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
      onSubmit={saveTariff}
    >
      <h3 style={{ color: "var(--text-optional-color)" }}>{id ? "Editar Tarifa" : "Nueva Tarifa"}</h3>
      <FormControl fullWidth>
        <CustomTextField
          label="Vueltas"
          value={laps}
          onChange={(e) => setLaps(e.target.value)}
        />
      </FormControl>
      <FormControl fullWidth>
        <CustomTextField
          label="Máx. Minutos"
          value={max_minutes}
          onChange={(e) => setMax_minutes(e.target.value)}
        />
      </FormControl>

      <FormControl fullWidth>
        <CustomTextField
          label="Precio Regular"
          value={regular_price}
          onChange={(e) => setRegular_price(e.target.value)}
        />
      </FormControl>
      <FormControl fullWidth>
        <CustomTextField
          label="Duración Total (minutos)"
          value={total_duration}
          onChange={(e) => setTotal_duration(e.target.value)}
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
          onClick={() => navigate("/tariff/list")}
          startIcon={<ArrowBackIcon />}
        >
          Volver
        </Button>
      </FormControl>
    </Box>
  );
};

export default AddEditTariff;