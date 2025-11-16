import { useMutation, useQueryClient } from '@tanstack/react-query'
import { updateWorkflow } from '../api/capa.service'

export default function useWorkflowVars() {
  const qc = useQueryClient()
  return useMutation<any, Error, { id: string | number; vars: Record<string, any> }>({
    mutationFn: ({ id, vars }) => updateWorkflow({ id, vars }),
    onSuccess: (_data, variables) => {
      qc.invalidateQueries({ queryKey: ['capa', 'detail', variables.id] })
    },
  })
}