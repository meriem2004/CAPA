import { useMutation, useQueryClient } from '@tanstack/react-query';
import { updateStatus } from '../services/api/capa.service';

export default function useUpdateStatus() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: updateStatus,
    onSuccess: (_data, variables) => {
      qc.invalidateQueries({ queryKey: ['capa','detail', variables.id] });
      qc.invalidateQueries({ queryKey: ['capa','list'] });
    },
  });
}