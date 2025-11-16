import api from './http';

export const createCapa = async (payload: any) => {
  const { data } = await api.post('/api/capa', payload);
  return data;
};

export const listCapas = async () => {
  const { data } = await api.get('/api/capa');
  return data;
};

export const getCapa = async (id: string | number) => {
  const { data } = await api.get(`/api/capa/${id}`);
  return data;
};

export const updateStatus = async ({ id, status }: { id: string | number; status: string }) => {
  const { data } = await api.patch(`/api/capa/${id}/status`, null, { params: { status } });
  return data;
};

export const updateWorkflow = async ({ id, vars }: { id: string | number; vars: Record<string, any> }) => {
  const { data } = await api.patch(`/api/capa/${id}/workflow`, null, { params: vars });
  return data;
};