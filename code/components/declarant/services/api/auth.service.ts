import api from './http';

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  firstName?: string;
  lastName?: string;
  role: 'MANAGER' | 'OPERATOR' | 'QUALITY';
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  id: number;
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
  role: 'MANAGER' | 'OPERATOR' | 'QUALITY';
  message: string;
}

export const register = async (user: RegisterRequest) => {
  const { data } = await api.post<RegisterRequest>('/api/auth/register', user);
  return data;
};

export const login = async (credentials: LoginRequest): Promise<LoginResponse> => {
  const { data } = await api.post<LoginResponse>('/api/auth/login', credentials);
  return data;
};