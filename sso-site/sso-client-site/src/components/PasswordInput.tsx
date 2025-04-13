import { Input, InputLabel, FormControl, InputAdornment, IconButton, FormHelperText } from '@mui/material';
import { Visibility, VisibilityOff } from '@mui/icons-material';
import { useState, useImperativeHandle, forwardRef } from 'react';

export interface PasswordInputProps {
  value: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  defaultShowPassword?: boolean;
  label?: string;
  id?: string;
}

export interface PasswordInputRef {
  validate: () => boolean;
}


export const PasswordInput = forwardRef<PasswordInputRef, PasswordInputProps>(
  (
    {
      value,
      onChange,
      defaultShowPassword = false,
      label = 'password',
      id = 'password-input'
    },
    ref
  ) => {
  const [showPassword,setShowPassword] = useState(defaultShowPassword)
  const [error, setError] = useState(false);
  const [helperText, setHelperText] = useState('');

  const togglePasswordVisibility = () => {
    setShowPassword(prev => !prev);
  };

  const validate = (): boolean => {
    if (value.length === 0) {
      setError(true);
      setHelperText(`${label} is required`);
      return false;
    } else if (value.length < 6) {
      setError(true);
      setHelperText(`${label} must be at least 6 characters`);
      return false;
    } 
    setError(false);
    setHelperText('');
    return true;
  };

  useImperativeHandle(ref, () => ({
    validate,
  }));

  return (
    <FormControl sx={{ m: 1, width: '25ch' }} variant="standard">
      <InputLabel htmlFor={id}>{label}</InputLabel>
      <Input
        id={id}
        type={showPassword ? 'text' : 'password'}
        value={value}
        error={error}
        onChange={onChange}
        endAdornment={
          <InputAdornment position="end">
            <IconButton onClick={togglePasswordVisibility}>
              {showPassword ? <VisibilityOff /> : <Visibility />}
            </IconButton>
          </InputAdornment>
        }
      />
      {error && <FormHelperText>{helperText}</FormHelperText>}
    </FormControl>
  );
}
);