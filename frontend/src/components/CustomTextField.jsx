import TextField from "@mui/material/TextField";

const CustomTextField = ({ label, value, onChange, type = "text", helperText, ...props }) => {
  return (
    <TextField
      label={label}
      value={value}
      onChange={onChange}
      type={type}
      helperText={helperText}
      variant="outlined" // Mantener el estilo de caja
      fullWidth
      margin="normal"
      sx={{
        "& .MuiOutlinedInput-root": {
          color: "var(--text-color)", // Color del texto
          "& fieldset": {
            borderColor: "var(--border-color)", // Color del borde por defecto
          },
          "&:hover fieldset": {
            borderColor: "var(--accent-color)", // Color del borde al pasar el mouse
          },
          "&.Mui-focused fieldset": {
            borderColor: "var(--secondary-color)", // Color del borde al estar enfocado
            borderWidth: "2px", // Borde más grueso al estar enfocado
          },
          "&.Mui-focused": {
            backgroundColor: "rgba(160, 76, 255, 0.2)", // Fondo más claro al estar enfocado
          },
        },
        "& .MuiFormLabel-root": {
          color: "var(--text-color)", // Color de la etiqueta
        },
      }}
      {...props} // Permitir pasar otras propiedades como `InputLabelProps`
    />
  );
};

export default CustomTextField;