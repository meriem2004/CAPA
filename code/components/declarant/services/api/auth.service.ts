import api from './http';

export const register = async (user: any) => {
  const { data } = await api.post('/api/auth/register', user);
  return data;
};