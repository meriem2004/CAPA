import { useQuery } from '@tanstack/react-query'
import { listCapas } from '../api/capa.service'

export default function useCapaList() {
  return useQuery({ queryKey: ['capa', 'list'], queryFn: listCapas })
}