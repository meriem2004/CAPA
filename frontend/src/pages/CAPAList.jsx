import { Link } from 'react-router-dom';
import useCapaList from '../hooks/useCapaList';
import useCreateCapa from '../hooks/useCreateCapa';

function makeMockCapa() {
  const rand = Math.floor(Math.random() * 1000);
  const severities = ['minor', 'major', 'critical'];
  const severity = severities[Math.floor(Math.random() * severities.length)];
  // Ensure capaNumber length <= 20 to satisfy backend constraint
  const shortId = Math.random().toString(36).slice(2, 10); // 8 chars
  const capaNumber = `CAPA-${shortId}-${rand}`.slice(0, 20);
  return {
    capaNumber,
    title: `Mock CAPA ${rand}`,
    description: 'Generated mock CAPA to test BPMN flow and backend integration.',
    capaType: Math.random() > 0.5 ? 'corrective' : 'preventive',
    severity,
    necessiteCapa: Math.random() > 0.5,
    planApprouve: Math.random() > 0.5,
    rejectCount: Math.floor(Math.random() * 3),
    efficace: Math.random() > 0.5,
    besoinFormation: Math.random() > 0.5,
  };
}

export default function CAPAList() {
  const { data, isLoading, isError } = useCapaList();
  const createCapa = useCreateCapa();

  const onAddMock = () => {
    const payload = makeMockCapa();
    createCapa.mutate(payload);
  };

  if (isLoading) return <div>Loading CAPA...</div>;
  if (isError) return <div>Failed to load CAPA list.</div>;

  return (
    <div className="container">
      <h2>CAPA List</h2>
      <div style={{ marginBottom: 12 }}>
        <button onClick={onAddMock} disabled={createCapa.isPending}>
          {createCapa.isPending ? 'Creating...' : 'Add Mock CAPA'}
        </button>
      </div>
      <table>
        <thead>
          <tr>
            <th>#</th>
            <th>Title</th>
            <th>Severity</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {data?.map((c) => (
            <tr key={c.id}>
              <td>{c.capaNumber}</td>
              <td>{c.title}</td>
              <td>{c.severity}</td>
              <td>{c.currentStatus}</td>
              <td>
                <Link to={`/${c.id}`}>View</Link>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}