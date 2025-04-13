import { FormControl, InputLabel, Input } from '@mui/material';

export function UsernameInput({ 
    value,
    onChange,
    label = "username",
    id = "username-input"
}: {
    value: string;
    onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
    label?: string;
    id?: string;
}) {
  return (
    <FormControl sx={{ m: 1, width: '25ch' }} variant="standard">
      <InputLabel htmlFor={id}>{label}</InputLabel>
      <Input
        id={id}
        value={value}
        onChange={onChange}
        type="text"
      />
    </FormControl>
  );
}