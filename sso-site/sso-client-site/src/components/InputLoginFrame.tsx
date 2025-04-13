import React from 'react';
import { Box, Button, Typography } from '@mui/material';
import { LoginApi } from '@api/UserApi';
import { UsernameInput } from './UsernameInput';
import { PasswordInput, PasswordInputRef } from './PasswordInput';
import './InputLoginFrame.css';

export function LoginForm() {
  const [username, setUsername] = React.useState('');
  const [password, setPassword] = React.useState('');
  const [loading, setLoading] = React.useState(false);
  const passwordRef = React.useRef<PasswordInputRef>(null);


  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
  
    const isPasswordValid = passwordRef.current?.validate() ?? true;
    if (!isPasswordValid) return;
  
    setLoading(true);
    try {
      await LoginApi(username, password);
    } catch (err) {
      //TODO set error alert
      console.error('Login failed:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className='form-container'>
    <Box
      component="form"
      onSubmit={handleSubmit}
      autoComplete="off"
    >
      <Typography className='form-title' variant="h5" gutterBottom>Sign up</Typography>

      <UsernameInput value={username} onChange={(e) => setUsername(e.target.value)} />
      <PasswordInput
        ref={passwordRef}
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        defaultShowPassword={false}
      />

      <Button
        type="submit"
        variant="contained"
        color="primary"
        sx={{ m: 1, width: '25ch' }}
        disabled={loading}
      >
        {loading ? 'Signing up…' : 'Sign up'}
      </Button>
    </Box>
    
  </div>
  );
}
