import { Link } from 'react-router-dom';
import useCapaList from '../hooks/useCapaList';

export default function CAPAList() {
  const { data, isLoading, isError } = useCapaList();

  if (isLoading) return <div>Loading CAPA...</div>;
  if (isError) return <div>Failed to load CAPA list.</div>;

  return (
    <div className="container">
      <h2>CAPA List</h2>
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