
const LoginApi = async (username: string, password: string) => {
    try {
      await new Promise(resolve => setTimeout(resolve, 1000));
      console.log('✅ Login Success:', { username, password });
    } catch (e) {
      console.error('❌ Login Failed', e);
    }
  };

export {LoginApi}