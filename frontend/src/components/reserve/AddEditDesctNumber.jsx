import { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import desctNumberService from "../../services/desctnumber.service";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import SaveIcon from "@mui/icons-material/Save";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import CustomTextField from "../CustomTextField";
import FormControl from "@mui/material/FormControl";

const AddEditDesctNumber = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const editing = location.state !== null;

  const [minpersonas, setMinpersonas] = useState(editing ? location.state.minpersonas : 0);
  const [maxpersonas, setMaxpersonas] = useState(editing ? location.state.maxpersonas : 0);
  const [porcentajedesct, setPorcentajedesct] = useState(editing ? location.state.porcentajedesct : 0);

  const saveDesct = (e) => {
    e.preventDefault();
    const desct = { minpersonas, maxpersonas, porcentajedesct };
    if (editing) {
      desctNumberService.createDesctNumber({ ...desct, id: location.state.id })
        .then(() => navigate("/desctnumber/list"))
        .catch((error) => console.error("Error al actualizar descuento:", error));
    } else {
      desctNumberService.createDesctNumber(desct)
        .then(() => navigate("/desctnumber/list"))
        .catch((error) => console.error("Error al crear descuento:", error));
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
      onSubmit={saveDesct}
    >
      <h3 style={{ color: "var(--text-optional-color)" }}>{editing ? "Editar Descuento" : "Nuevo Descuento"}</h3>
      <FormControl fullWidth>
        <CustomTextField
          label="Mín. Personas"
          type="number"
          value={minpersonas}
          onChange={(e) => setMinpersonas(Number(e.target.value))}
        />
      </FormControl>
      <FormControl fullWidth>
        <CustomTextField
          label="Máx. Personas"
          type="number"
          value={maxpersonas}
          onChange={(e) => setMaxpersonas(Number(e.target.value))}
        />
      </FormControl>
      <FormControl fullWidth>
        <CustomTextField
          label="Porcentaje Descuento (%)"
          type="number"
          value={porcentajedesct}
          onChange={(e) => setPorcentajedesct(Number(e.target.value))}
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
          onClick={() => navigate("/desctnumber/list")}
          startIcon={<ArrowBackIcon />}
        >
          Volver
        </Button>
      </FormControl>
    </Box>
  );
};

export default AddEditDesctNumber;