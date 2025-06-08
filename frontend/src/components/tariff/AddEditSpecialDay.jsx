import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import specialDayService from "../../services/specialday.service";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import SaveIcon from "@mui/icons-material/Save";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import CustomTextField from "../CustomTextField";
import FormControl from "@mui/material/FormControl";

const AddEditSpecialDay = () => {
  const [date, setDate] = useState("");
  const [description, setDescription] = useState("");
  const { id } = useParams();
  const navigate = useNavigate();

  useEffect(() => {
    if (id) {
      specialDayService
        .getSpecialDayById(id)
        .then((response) => {
          const specialDay = response.data;
          setDate(specialDay.date);
          setDescription(specialDay.description);
        })
        .catch((error) => {
          console.error("Error al cargar el día especial:", error);
        });
    }
  }, [id]);

  const saveSpecialDay = (e) => {
    e.preventDefault();
    const specialDay = { date, description };

    if (id) {
      specialDayService
        .createSpecialDay({ ...specialDay, id })
        .then(() => {
          navigate("/specialdays/list");
        })
        .catch((error) => {
          console.error("Error al actualizar el día especial:", error);
        });
    } else {
      specialDayService
        .createSpecialDay(specialDay)
        .then(() => {
          navigate("/specialdays/list");
        })
        .catch((error) => {
          console.error("Error al crear el día especial:", error);
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
      onSubmit={saveSpecialDay}
    >
      <h3 style={{ color: "var(--text-optional-color)" }}>
        {id ? "Editar Día Especial" : "Nuevo Día Especial"}
      </h3>
      <FormControl fullWidth>
        <CustomTextField
          label="Fecha"
          type="date"
          value={date}
          onChange={(e) => setDate(e.target.value)}
          InputLabelProps={{ shrink: true }}
        />
      </FormControl>
      <FormControl fullWidth>
        <CustomTextField
          label="Descripción"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
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
          onClick={() => navigate("/specialdays/list")}
          startIcon={<ArrowBackIcon />}
        >
          Volver
        </Button>
      </FormControl>
    </Box>
  );
};

export default AddEditSpecialDay;