import { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import desctFrecService from "../../services/desctfrec.service";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import SaveIcon from "@mui/icons-material/Save";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import CustomTextField from "../CustomTextField";
import FormControl from "@mui/material/FormControl";

const AddEditDesctFrec = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const editing = location.state !== null;

  const [minveces, setMinveces] = useState(editing ? location.state.minveces : 0);
  const [maxveces, setMaxveces] = useState(editing ? location.state.maxveces : 0);
  const [porcentajedesct, setPorcentajedesct] = useState(editing ? location.state.porcentajedesct : 0);

  const saveDesct = (e) => {
    e.preventDefault();
    const desct = { minveces, maxveces, porcentajedesct };
    if (editing) {
      desctFrecService.createDesctFrecu({ ...desct, id: location.state.id })
        .then(() => navigate("/desctfrec/list"))
        .catch((error) => console.error("Error al actualizar descuento:", error));
    } else {
      desctFrecService.createDesctFrecu(desct)
        .then(() => navigate("/desctfrec/list"))
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
          label="Mín. Veces"
          type="number"
          value={minveces}
          onChange={(e) => setMinveces(Number(e.target.value))}
        />
      </FormControl>
      <FormControl fullWidth>
        <CustomTextField
          label="Máx. Veces"
          type="number"
          value={maxveces}
          onChange={(e) => setMaxveces(Number(e.target.value))}
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
          onClick={() => navigate("/desctfrec/list")}
          startIcon={<ArrowBackIcon />}
        >
          Volver
        </Button>
      </FormControl>
    </Box>
  );
};

export default AddEditDesctFrec;