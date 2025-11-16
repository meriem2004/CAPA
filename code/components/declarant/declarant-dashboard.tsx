"use client"

import { Card } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { DeclarantMockCapaPage } from './declarant-mock-capa-page'

interface TaskCard {
  id: string
  capaNumber: string
  taskName: string
  department: string
  priority: 'high' | 'medium' | 'low'
  status: 'pending' | 'overdue' | 'completed'
  timeRemaining: string
}

const mockTasks: TaskCard[] = [
  {
    id: '1',
    capaNumber: 'CAPA-2024-001',
    taskName: 'Investigate equipment failure',
    department: 'Manufacturing',
    priority: 'high',
    status: 'overdue',
    timeRemaining: '2 days overdue',
  },
  {
    id: '2',
    capaNumber: 'CAPA-2024-002',
    taskName: 'Implement preventive measures',
    department: 'Quality',
    priority: 'medium',
    status: 'pending',
    timeRemaining: '5 days remaining',
  },
  {
    id: '3',
    capaNumber: 'CAPA-2024-003',
    taskName: 'Verify corrective action',
    department: 'Operations',
    priority: 'low',
    status: 'completed',
    timeRemaining: 'Completed',
  },
]

export function DeclarantDashboard() {
  const getPriorityColor = (priority: string) => {
    switch (priority) {
      case 'high':
        return 'bg-danger/10 text-danger'
      case 'medium':
        return 'bg-warning/10 text-warning'
      case 'low':
        return 'bg-success/10 text-success'
      default:
        return 'bg-muted text-muted-foreground'
    }
  }

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'pending':
        return '🟡'
      case 'overdue':
        return '🔴'
      case 'completed':
        return '✅'
      default:
        return '◯'
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-bold text-foreground">CAPA Tasks</h2>
        <p className="text-muted-foreground">{mockTasks.length} active tasks</p>
      </div>

      <div className="grid gap-4 md:grid-cols-1 lg:grid-cols-2">
        {mockTasks.map((task) => (
          <Card key={task.id} className="p-6 hover:shadow-md transition-shadow cursor-pointer">
            <div className="flex items-start justify-between mb-4">
              <div>
                <p className="text-xs text-muted-foreground font-semibold uppercase">{task.capaNumber}</p>
                <h3 className="text-lg font-semibold text-foreground mt-1">{task.taskName}</h3>
              </div>
              <span className="text-2xl">{getStatusIcon(task.status)}</span>
            </div>

            <div className="space-y-3">
              <div className="flex items-center justify-between text-sm">
                <span className="text-muted-foreground">{task.department}</span>
                <span className={`px-3 py-1 rounded-full text-xs font-semibold ${getPriorityColor(task.priority)}`}>
                  {task.priority.charAt(0).toUpperCase() + task.priority.slice(1)}
                </span>
              </div>
              <p className={`text-sm font-medium ${
                task.status === 'overdue' ? 'text-danger' : 'text-success'
              }`}>
                {task.timeRemaining}
              </p>
            </div>
          </Card>
        ))}
      </div>

      {/* Mock CAPA quick actions */}
      <div className="border rounded-lg p-4">
        <div className="flex items-center justify-between mb-3">
          <h3 className="text-lg font-semibold text-foreground">Quick: Create Mock CAPA</h3>
          <Button asChild variant="outline" size="sm">
            <a href="#mock-capa">Scroll to section</a>
          </Button>
        </div>
        <div id="mock-capa">
          <DeclarantMockCapaPage />
        </div>
      </div>
    </div>
  )
}
