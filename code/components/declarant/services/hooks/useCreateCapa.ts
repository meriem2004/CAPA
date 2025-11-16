import { useMutation, useQueryClient } from '@tanstack/react-query'
import { createCapa } from '../api/capa.service'

export default function useCreateCapa() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: createCapa,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['capa', 'list'] })
    },
  })
}