import { useMutation, useQueryClient } from '@tanstack/react-query';
import { updateWorkflow } from '../services/api/capa.service';

export default function useWorkflowVars() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: updateWorkflow,
    onSuccess: (_data, variables) => {
      qc.invalidateQueries({ queryKey: ['capa','detail', variables.id] });
    },
  });
}