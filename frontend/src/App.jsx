import { Link, Route, Routes } from 'react-router-dom'
import './App.css'
import CAPAList from './pages/CAPAList'
import CAPADetail from './pages/CAPADetail'

function App() {
  return (
    <div>
      <nav style={{ padding: 12, borderBottom: '1px solid #eee' }}>
        <Link to="/" style={{ marginRight: 12 }}>CAPA</Link>
        <a href="http://localhost:8080/api/tasks/ping" target="_blank" rel="noreferrer">Backend Ping</a>
      </nav>
      <div style={{ padding: 16 }}>
        <Routes>
          <Route path="/" element={<CAPAList />} />
          <Route path=":id" element={<CAPADetail />} />
        </Routes>
      </div>
    </div>
  )
}

export default App
