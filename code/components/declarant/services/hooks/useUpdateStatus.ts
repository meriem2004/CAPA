import { useMutation, useQueryClient } from '@tanstack/react-query'
import { updateStatus } from '../api/capa.service'

export default function useUpdateStatus() {
  const qc = useQueryClient()
  return useMutation<any, Error, { id: string | number; status: string }>({
    mutationFn: ({ id, status }) => updateStatus({ id, status }),
    onSuccess: (_data, variables) => {
      qc.invalidateQueries({ queryKey: ['capa', 'detail', variables.id] })
      qc.invalidateQueries({ queryKey: ['capa', 'list'] })
    },
  })
}