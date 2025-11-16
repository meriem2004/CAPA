import api from './http';

export const ping = async () => {
  const { data } = await api.get('/api/tasks/ping');
  return data;
};