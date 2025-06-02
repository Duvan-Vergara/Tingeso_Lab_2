import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import reserveService from "../services/reserve.service";
import tariffService from "../services/tariff.service";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import FormControl from "@mui/material/FormControl";
import MenuItem from "@mui/material/MenuItem";
import SaveIcon from "@mui/icons-material/Save";
import Autocomplete from "@mui/material/Autocomplete";
import userService from "../services/user.service";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import CustomTextField from "./CustomTextField";

const AddEditReserve = () => {
  const [clientName, setClientName] = useState("");
  const [date, setDate] = useState("");
  const [tariffId, setTariffId] = useState("");
  const [tariffs, setTariffs] = useState([]);
  const [finalPrice, setFinalPrice] = useState(0.0);
  const [users, setUsers] = useState([]);
  const [selectedUsers, setSelectedUsers] = useState([]);
  const [beginTime, setBeginTime] = useState("");
  const [finish, setFinishTime] = useState("");
  const { id } = useParams();
  const navigate = useNavigate();

  const loadUsers = () => {
    userService
      .getAllUsers()
      .then((response) => {
        setUsers(response.data);
        console.log("Usuarios cargados:", response.data);
      })
      .catch((error) => {
        console.error("Error al cargar usuarios:", error);
      });
  };

  // Cargar tarifas disponibles
  const loadTariffs = () => {
    tariffService
      .getAllTariffs()
      .then((response) => {
        setTariffs(response.data);
        console.log("Tarifas cargadas:", response.data);
      })
      .catch((error) => {
        console.error("Error al cargar tarifas:", error);
      });
  };

  // Cargar datos de la reserva si se está editando
  useEffect(() => {
    loadTariffs();
    loadUsers();
    console.log("Datos Cargados");
    if (id) {
      reserveService
        .getReserveById(id)
        .then((response) => {
          const reserve = response.data;
          console.log("Reserva cargada:", reserve);
          console.log("Precio final cargado:", reserve.finalPrice);
          setClientName(reserve.group?.[0]?.name || ""); // Asumir que el primer usuario es el creador
          setDate(reserve.date);
          setTariffId(reserve.tariff?.id || "");
          setFinalPrice(reserve.finalPrice || 0);
          setSelectedUsers(reserve.group || []); // Cargar usuarios del grupo
          setBeginTime(reserve.begin || ""); // Extraer hora de inicio
          setFinishTime(reserve.finish || ""); // Extraer hora de finalización
        })
        .catch((error) => {
          console.error("Error al cargar la reserva:", error);
        });
    }
  }, [id]);

  // Guardar o actualizar la reserva
  const saveReserve = (e) => {
    e.preventDefault();

    if (!date || !tariffId || !beginTime || !finish) {
      alert("Por favor, complete todos los campos obligatorios.");
      return;
    }
    const selectedTariff = tariffs.find((tariff) => tariff.id === tariffId);

    const reserve = {
      date,
      begin: `${date}T${beginTime}`,
      finish: `${date}T${finish}`,
      group: selectedUsers,
      tariff: selectedTariff,
      finalPrice: finalPrice,
    };

    if (id) {
      // Actualizar reserva existente
      reserveService
        .saveReserve({ ...reserve, id })
        .then((response) => {
          console.log("Reserva actualizada:", response.data);
          navigate("/reserve/list");
        })
        .catch((error) => {
          console.error("Error al actualizar la reserva:", error);
        });
    } else {
      // Crear nueva reserva
      reserveService
        .saveReserve(reserve)
        .then((response) => {
          console.log("Reserva creada:", response.data);
          navigate("/reserve/list");
        })
        .catch((error) => {
          console.error("Error al crear la reserva:", error);
        });
    }
  };

  // Recalcular el precio final al cambiar los valores clave
  const recalculatePrice = () => {

    // Si no hay tarifa o usuarios seleccionados, no se puede calcular el precio
    if (!tariffId || selectedUsers.length === 0) {
      setFinalPrice(0);
      return;
    }
    const selectedTariff = tariffs.find((tariff) => tariff.id === tariffId);

    // Construir el objeto de reserva con los datos disponibles
    const reserve = {
      date: date,
      begin: date && beginTime ? `${date}T${beginTime}` : null, // Validar hora de inicio
      finish: date && finish ? `${date}T${finish}` : null, // Validar hora de finalización
      tariff: selectedTariff,
      group: selectedUsers,
    };

    console.log("Datos enviados para calcular el precio:", reserve);

    reserveService
      .calculateFinalPrice(reserve)
      .then((response) => {
        console.log("Precio final calculado:", response.data);
        setFinalPrice(response.data);
      })
      .catch((error) => {
        console.error("Error al calcular el precio final:", error);
        setFinalPrice(0); // Si hay un error, establecer el precio a 0
      });
  };

  useEffect(() => {
    console.log("Recalculando precio...");
    recalculatePrice();
  }, [date, beginTime, finish, tariffId, selectedUsers]);

  useEffect(() => {
    if (clientName) {
      const matchingUser = users.find((user) => user.name === clientName);
      if (matchingUser) {
        setSelectedUsers((prevSelectedUsers) => {
          const filteredUsers = prevSelectedUsers.filter((user) => user.id !== matchingUser.id);
          return [matchingUser, ...filteredUsers];
        });
      }
    }
  }, [clientName, users]);

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
      onSubmit={saveReserve}

    >
      <h3 style={{color: "var(--text-optional-color)"}}> {id ? "Editar Reserva" : "Nueva Reserva"}</h3>
      <FormControl fullWidth>
        <CustomTextField
          id="clientName"
          label="Nombre del Cliente"
          value={clientName}
          onChange={(e) => setClientName(e.target.value)}
        />
      </FormControl>

      <FormControl fullWidth>
        <CustomTextField
          id="date"
          label="Fecha"
          type="date"
          value={date}
          onChange={(e) => { setDate(e.target.value) }}
          InputLabelProps={{
            shrink: true,
          }}
        />
      </FormControl>
      <FormControl fullWidth>
        <CustomTextField
          id="begin"
          label="Hora de Inicio"
          type="time"
          value={beginTime}
          onChange={(e) => setBeginTime(e.target.value)}
          InputLabelProps={{
            shrink: true,
          }}
        />
      </FormControl>
      <FormControl fullWidth>
        <CustomTextField
          id="finish"
          label="Hora de Finalización"
          type="time"
          value={finish}
          onChange={(e) => setFinishTime(e.target.value)}
          InputLabelProps={{
            shrink: true,
          }}
        />
      </FormControl>

      <FormControl fullWidth>
        <Autocomplete
          id="group"
          multiple
          options={users}
          getOptionLabel={(user) => `${user.name} - ${user.rut}`}
          filterOptions={(options, { inputValue }) =>
            options.filter(
              (user) =>
                user.name.toLowerCase().includes(inputValue.toLowerCase()) ||
                user.rut.includes(inputValue)
            )
          }
          value={selectedUsers}
          onChange={(event, newValue) => setSelectedUsers(newValue)}
          isOptionEqualToValue={(option, value) => option.id === value.id} // Comparar por ID
          renderInput={(params) => (
            <CustomTextField {...params} label="Seleccionar Usuarios" variant="standard" />
          )}
        />
      </FormControl>
      <FormControl fullWidth>
        <CustomTextField
          id="tariff"
          label="Tarifa"
          select
          value={tariffId}
          onChange={(e) => setTariffId(e.target.value)}
        >
          {tariffs.map((tariff) => (
            <MenuItem key={tariff.id} value={tariff.id}>
              {`${tariff.laps} vueltas / ${tariff.maxMinutes} minutos`}
            </MenuItem>
          ))}
        </CustomTextField>
      </FormControl>

      <FormControl fullWidth>
        <CustomTextField
          id="finalPrice"
          label="Precio Final"
          value={finalPrice}
          InputProps={{
            readOnly: true,
          }}
        />
      </FormControl>

      <FormControl>
        <br />
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
          onClick={() => navigate("/reserve/list")}
          startIcon={<ArrowBackIcon />}
        >
          Volver
        </Button>
      </FormControl>
    </Box>
  );
};

export default AddEditReserve;