import { useQuery } from '@tanstack/react-query';
import { getCapa } from '../services/api/capa.service';

export default function useCapaDetail(id) {
  return useQuery({ queryKey: ['capa','detail', id], queryFn: () => getCapa(id), enabled: !!id });
}