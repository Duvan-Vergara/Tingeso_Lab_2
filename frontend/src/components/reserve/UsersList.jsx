import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import userService from "../../services/user.service";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell, { tableCellClasses } from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import Button from "@mui/material/Button";
import PersonAddIcon from "@mui/icons-material/PersonAdd";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";

const UserList = () => {
  const [users, setUsers] = useState([]);

  const navigate = useNavigate();

  const init = () => {
    userService
      .getAllUsers()
      .then((response) => {
        console.log("Mostrando listado de todos los usuarios.", response.data);
        setUsers(response.data);
      })
      .catch((error) => {
        console.log(
          "Se ha producido un error al intentar mostrar listado de todos los usuarios.",
          error
        );
      });
  };

  useEffect(() => {
    console.log("Cargando listado de usuarios.");
    init();
  }, []);

  const handleDelete = (id) => {
    console.log("Printing id", id);
    const confirmDelete = window.confirm(
      "¿Esta seguro que desea borrar este usuario?"
    );
    if (confirmDelete) {
      userService
        .remove(id)
        .then((response) => {
          console.log("Usuario ha sido eliminado.", response.data);
          init();
        })
        .catch((error) => {
          console.log(
            "Se ha producido un error al intentar eliminar al usuario",
            error
          );
        });
    }
  };

  const handleEdit = (id) => {
    console.log("Printing id", id);
    navigate(`/user/edit/${id}`);
  };

  return (
    <TableContainer component={Paper} sx={{ backgroundColor: "rgba(30, 30, 47, 0.9)" }}>
      <h3 style={{ color: "var(--text-optional-color)", textAlign: "center" }}>Lista de Usuarios</h3>
      <Button
        variant="contained"
        sx={{
          backgroundColor: "var(--primary-color)",
          color: "var(--text-color)",
          "&:hover": { backgroundColor: "var(--hover-color)" },
        }}
        startIcon={<PersonAddIcon />}
        onClick={() => navigate("/user/add")}
        style={{ margin: "1rem" }}
      >
        Añadir Usuario
      </Button>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell sx={{ color: "var(--text-color)", fontWeight: "bold" }}>Rut</TableCell>
            <TableCell sx={{ color: "var(--text-color)", fontWeight: "bold" }}>Nombre</TableCell>
            <TableCell sx={{ color: "var(--text-color)", fontWeight: "bold" }}>Apellido</TableCell>
            <TableCell sx={{ color: "var(--text-color)", fontWeight: "bold" }}>Email</TableCell>
            <TableCell sx={{ color: "var(--text-color)", fontWeight: "bold" }}>Fecha de Nacimiento</TableCell>
            <TableCell align="center" sx={{ color: "var(--text-color)", fontWeight: "bold" }}>Operaciones</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {users.map((user) => (
            <TableRow key={user.id}>
              <TableCell sx={{ color: "var(--text-color)" }}>{user.rut}</TableCell>
              <TableCell sx={{ color: "var(--text-color)" }}>{user.name}</TableCell>
              <TableCell sx={{ color: "var(--text-color)" }}>{user.lastName}</TableCell>
              <TableCell sx={{ color: "var(--text-color)" }}>{user.email}</TableCell>
              <TableCell sx={{ color: "var(--text-color)" }}>{user.birthDate}</TableCell>
              <TableCell align="center">
                <Button
                  variant="contained"
                  sx={{
                    backgroundColor: "var(--primary-color)",
                    color: "var(--text-color)",
                    "&:hover": { backgroundColor: "var(--hover-color)" },
                  }}
                  size="small"
                  onClick={() => handleEdit(user.id)}
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
                  onClick={() => handleDelete(user.id)}
                  style={{ marginLeft: "0.5rem" }}
                  startIcon={<DeleteIcon />}
                >
                  Eliminar
                </Button>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default UserList;