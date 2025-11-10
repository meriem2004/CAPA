import { useParams } from 'react-router-dom';
import useCapaDetail from '../hooks/useCapaDetail';
import useUpdateStatus from '../hooks/useUpdateStatus';
import useWorkflowVars from '../hooks/useWorkflowVars';

export default function CAPADetail() {
  const { id } = useParams();
  const { data, isLoading, isError } = useCapaDetail(id);
  const updateStatus = useUpdateStatus();
  const updateWorkflow = useWorkflowVars();

  if (isLoading) return <div>Loading...</div>;
  if (isError) return <div>Failed to load CAPA.</div>;

  const c = data;
  const onStatusChange = (status) => updateStatus.mutate({ id, status });
  const onWorkflowUpdate = () => {
    const vars = {
      necessiteCapa: !!c.necessiteCapa,
      planApprouve: !!c.planApprouve,
      rejectCount: c.rejectCount ?? 0,
      efficace: !!c.efficace,
      besoinFormation: !!c.besoinFormation,
    };
    updateWorkflow.mutate({ id, vars });
  };

  return (
    <div className="container">
      <h2>CAPA Detail</h2>
      <div>
        <strong>{c.capaNumber}</strong> - {c.title}
      </div>
      <div>Severity: {c.severity}</div>
      <div>Status: {c.currentStatus}</div>
      <div>ProcessInstanceKey: {c.processInstanceKey}</div>

      <div style={{ marginTop: 16 }}>
        <button onClick={() => onStatusChange('analysis')}>Set to Analysis</button>
        <button onClick={() => onStatusChange('planned')}>Set to Planned</button>
        <button onClick={() => onStatusChange('execution')}>Set to Execution</button>
        <button onClick={() => onStatusChange('closed')}>Close</button>
      </div>

      <h3 style={{ marginTop: 24 }}>Workflow Variables</h3>
      <div>necessiteCapa: {String(c.necessiteCapa)}</div>
      <div>planApprouve: {String(c.planApprouve)}</div>
      <div>rejectCount: {c.rejectCount}</div>
      <div>efficace: {String(c.efficace)}</div>
      <div>besoinFormation: {String(c.besoinFormation)}</div>
      <button onClick={onWorkflowUpdate} style={{ marginTop: 8 }}>Apply Workflow Update</button>
    </div>
  );
}