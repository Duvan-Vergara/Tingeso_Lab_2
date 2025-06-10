import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import reserveService from "../../services/reserve.service";
import tariffService from "../../services/tariff.service";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import FormControl from "@mui/material/FormControl";
import MenuItem from "@mui/material/MenuItem";
import SaveIcon from "@mui/icons-material/Save";
import Autocomplete from "@mui/material/Autocomplete";
import userService from "../../services/user.service";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import CustomTextField from "../CustomTextField";

const AddEditReserve = () => {
  const [clientName, setClientName] = useState("");
  const [reserveday, setreserveday] = useState("");
  const [tariff_id, settariff_id] = useState("");
  const [tariffs, setTariffs] = useState([]);
  const [final_price, setfinal_price] = useState(0.0);
  const [users, setUsers] = useState([]);
  const [selectedUsers, setSelectedUsers] = useState([]);
  const [beginTime, setBeginTime] = useState("");
  const [finish, setFinishTime] = useState("");
  const { id } = useParams();
  const navigate = useNavigate();

  function formatDateToDDMMYYYY(dateStr) {
    if (!dateStr) return "";
    const [yyyy, mm, dd] = dateStr.split("-");
    return `${dd}-${mm}-${yyyy}`;
  }

  // Formatea de "dd-MM-yyyy" a "yyyy-MM-dd" para el input date
  function formatDateToYYYYMMDD(dateStr) {
    if (!dateStr) return "";
    const [dd, mm, yyyy] = dateStr.split("-");
    return `${yyyy}-${mm}-${dd}`;
  }

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
          console.log("Precio final cargado:", reserve.final_price);
          setClientName(reserve.reserves_users?.[0]?.name || ""); // Asumir que el primer usuario es el creador
          setreserveday(formatDateToYYYYMMDD(reserve.reserveday));
          settariff_id(reserve.tariff_id || ""); //la id de la tarifa asignada a la reserva
          setfinal_price(reserve.final_price || 0);
          setSelectedUsers(reserve.reserves_users || []); // Cargar usuarios del grupo
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

    if (!reserveday || !tariff_id || !beginTime || !finish) {
      alert("Por favor, complete todos los campos obligatorios.");
      return;
    }

    const reserve = {
      reserveday: formatDateToDDMMYYYY(reserveday),
      begin: beginTime,
      finish: finish,
      reserves_users: selectedUsers,
      tariff_id: tariff_id,
      final_price: final_price,
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
    if (!tariff_id || selectedUsers.length === 0 || !reserveday) {
      setfinal_price(0);
      return;
    }

    // Construir el objeto de reserva con los datos disponibles
    const reserve = {
      reserveday: formatDateToDDMMYYYY(reserveday),
      begin: beginTime,
      finish: finish,
      reserves_users: selectedUsers,
      tariff_id: tariff_id,
    };

    console.log("Datos enviados para calcular el precio:", reserve);

    reserveService
      .calculateFinalPrice(reserve)
      .then((response) => {
        console.log("Precio final calculado:", response.data);
        setfinal_price(response.data);
      })
      .catch((error) => {
        console.error("Error al calcular el precio final:", error);
        setfinal_price(0); // Si hay un error, establecer el precio a 0
      });
  };

  useEffect(() => {
    console.log("Recalculando precio...");
    recalculatePrice();
  }, [reserveday, beginTime, finish, tariff_id, selectedUsers]);

  useEffect(() => {
    // Si el grupo queda vacío, limpia el titular
    if (selectedUsers.length === 0) {
      setClientName("");
    } else {
      // Si el titular actual ya no está en el grupo, selecciona el primero
      const stillInGroup = selectedUsers.some(u => u.name === clientName);
      if (!stillInGroup) {
        setClientName(selectedUsers[0].name);
      }
    }
  }, [selectedUsers]);

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
          select
          id="clientName"
          label="Nombre del Cliente"
          value={clientName}
          onChange={(e) => setClientName(e.target.value)}
          required
          disabled={selectedUsers.length == 0}
          helperText={selectedUsers.length === 0 ? "Agrega al menos un usuario al grupo" : ""}
        >
          {selectedUsers.map((user) => (
            <MenuItem key={user.id} value={user.name}>
              {`${user.name} - ${user.rut}`}
            </MenuItem>
          ))}
        </CustomTextField>
      </FormControl>

      <FormControl fullWidth>
        <CustomTextField
          id="reserveday"
          label="Fecha"
          type="date"
          value={reserveday}
          onChange={(e) => { setreserveday(e.target.value) }}
          InputLabelProps={{
            shrink: true,
          }}
          required
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
          id="reserves_users"
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
          value={tariff_id}
          onChange={(e) => settariff_id(e.target.value)}
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
          id="final_price"
          label="Precio Final"
          value={final_price.toLocaleString("es-CL", { style: "currency", currency: "CLP" })}
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