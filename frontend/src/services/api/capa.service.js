import api from './http';

export const createCapa = async (payload) => {
  const { data } = await api.post('/api/capa', payload);
  return data;
};

export const listCapas = async () => {
  const { data } = await api.get('/api/capa');
  return data;
};

export const getCapa = async (id) => {
  const { data } = await api.get(`/api/capa/${id}`);
  return data;
};

export const updateStatus = async ({ id, status }) => {
  const { data } = await api.patch(`/api/capa/${id}/status`, null, { params: { status } });
  return data;
};

export const updateWorkflow = async ({ id, vars }) => {
  const { data } = await api.patch(`/api/capa/${id}/workflow`, null, { params: vars });
  return data;
};