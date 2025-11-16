import { useQuery } from '@tanstack/react-query'
import { getCapa } from '../api/capa.service'

export default function useCapaDetail(id?: string | number) {
  return useQuery({ queryKey: ['capa', 'detail', id], queryFn: () => getCapa(id!), enabled: !!id })
}