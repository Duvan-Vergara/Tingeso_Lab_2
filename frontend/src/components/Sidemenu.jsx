import * as React from "react";
import Box from "@mui/material/Box";
import Drawer from "@mui/material/Drawer";
import List from "@mui/material/List";
import Divider from "@mui/material/Divider";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import PeopleAltIcon from "@mui/icons-material/PeopleAlt";
import PaidIcon from "@mui/icons-material/Paid";
import DiscountIcon from "@mui/icons-material/Discount";
import MoreTimeIcon from "@mui/icons-material/MoreTime";
import HomeIcon from "@mui/icons-material/Home";
import { useNavigate } from "react-router-dom";

export default function Sidemenu({ open, toggleDrawer }) {
  const navigate = useNavigate();

  const listOptions = () => (
    <Box
      role="presentation"
      onClick={toggleDrawer(false)}
      sx={{
        backgroundColor: "rgba(30,30,47,0.9)",
        height: "100%",
        color: "var(--text-color)",
      }}
    >
      <List>
        <ListItemButton onClick={() => navigate("/home")}>
          <ListItemIcon>
            <HomeIcon sx={{color: "var(--text-optional-color)"}}/>
          </ListItemIcon>
          <ListItemText primary="Home" />
        </ListItemButton>

        <Divider />

        <ListItemButton onClick={() => navigate("/user/list")}>
          <ListItemIcon>
            <PeopleAltIcon sx={{ color: "var(--text-optional-color)" }} />
          </ListItemIcon>
          <ListItemText primary="Usuarios" />
        </ListItemButton>

        <ListItemButton onClick={() => navigate("/reserve/list")}>
          <ListItemIcon>
            <MoreTimeIcon sx={{ color: "var(--text-optional-color)" }} />
          </ListItemIcon>
          <ListItemText primary="Reservas" />
        </ListItemButton>

        <ListItemButton onClick={() => navigate("/specialdays/list")}>
          <ListItemIcon>
            <DiscountIcon sx={{ color: "var(--text-optional-color)" }} />
          </ListItemIcon>
          <ListItemText primary="Días Especiales" />
        </ListItemButton>

        <ListItemButton onClick={() => navigate("/tariff/list")}>
          <ListItemIcon>
            <PaidIcon sx={{ color: "var(--text-optional-color)" }} />
          </ListItemIcon>
          <ListItemText primary="Tarifas" />
        </ListItemButton>

        <ListItemButton onClick={() => navigate("/reports/generate")}>
          <ListItemIcon>
            <PaidIcon sx={{ color: "var(--text-optional-color)" }} />
          </ListItemIcon>
          <ListItemText primary="Generar Reporte" />
        </ListItemButton>

      </List>
      <Divider sx={{ backgroundColor: "var(--border-color)" }} />
    </Box>
  );

  return (
    <div>
      <Drawer anchor={"left"} open={open} onClose={toggleDrawer(false)}>
        {listOptions()}
      </Drawer>
    </div>
  );
}