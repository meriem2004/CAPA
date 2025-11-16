'use client'

import { Card } from '@/components/ui/card'

interface KPICard {
  label: string
  value: number
  icon: string
  color: string
}

const mockKPIs: KPICard[] = [
  { label: 'Open CAPAs', value: 12, icon: '📋', color: 'primary' },
  { label: 'Active RCA', value: 5, icon: '🔍', color: 'warning' },
  { label: 'Overdue', value: 3, icon: '⏰', color: 'danger' },
  { label: 'Closed This Month', value: 8, icon: '✅', color: 'success' },
]

interface TaskGroup {
  type: string
  count: number
  tasks: {
    id: string
    title: string
    dueDate: string
    priority: 'high' | 'medium' | 'low'
  }[]
}

const mockTaskGroups: TaskGroup[] = [
  {
    type: 'Root Cause Analysis',
    count: 5,
    tasks: [
      { id: '1', title: 'CAPA-2024-001: Equipment failure RCA', dueDate: '2024-11-20', priority: 'high' },
      { id: '2', title: 'CAPA-2024-002: Process deviation RCA', dueDate: '2024-11-22', priority: 'medium' },
    ],
  },
  {
    type: 'Action Plans',
    count: 3,
    tasks: [
      { id: '3', title: 'CAPA-2024-003: Implement controls', dueDate: '2024-11-25', priority: 'medium' },
    ],
  },
  {
    type: 'Effectiveness Checks',
    count: 2,
    tasks: [
      { id: '4', title: 'CAPA-2024-004: Verify corrective action', dueDate: '2024-12-01', priority: 'low' },
    ],
  },
]

export function QualiteDashboard() {
  const getPriorityColor = (priority: string) => {
    switch (priority) {
      case 'high':
        return 'border-l-4 border-l-danger'
      case 'medium':
        return 'border-l-4 border-l-warning'
      case 'low':
        return 'border-l-4 border-l-success'
      default:
        return ''
    }
  }

  return (
    <div className="space-y-8">
      {/* KPIs */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {mockKPIs.map((kpi) => (
          <Card key={kpi.label} className="p-4">
            <div className="flex items-start justify-between">
              <div>
                <p className="text-sm text-muted-foreground font-semibold">{kpi.label}</p>
                <p className="text-3xl font-bold text-foreground mt-2">{kpi.value}</p>
              </div>
              <span className="text-3xl">{kpi.icon}</span>
            </div>
          </Card>
        ))}
      </div>

      {/* Quick Filters */}
      <div className="flex gap-2 flex-wrap">
        {['All', 'Critical', 'Due Soon', 'Not Started', 'In Progress'].map((filter) => (
          <button
            key={filter}
            className="px-4 py-2 rounded-full border border-border hover:bg-muted transition-colors text-sm font-medium text-foreground"
          >
            {filter}
          </button>
        ))}
      </div>

      {/* Task Groups */}
      <div className="space-y-6">
        {mockTaskGroups.map((group) => (
          <div key={group.type} className="space-y-3">
            <h3 className="text-lg font-semibold text-foreground flex items-center gap-2">
              {group.type}
              <span className="bg-primary/20 text-primary text-sm font-bold px-2 py-1 rounded-full">
                {group.count}
              </span>
            </h3>
            <div className="space-y-2">
              {group.tasks.map((task) => (
                <Card key={task.id} className={`p-4 hover:shadow-md transition-shadow cursor-pointer ${getPriorityColor(task.priority)}`}>
                  <div className="flex items-center justify-between">
                    <div className="flex-1">
                      <p className="font-medium text-foreground">{task.title}</p>
                      <p className="text-xs text-muted-foreground mt-1">Due: {task.dueDate}</p>
                    </div>
                    <span className={`px-3 py-1 rounded-full text-xs font-semibold ${
                      task.priority === 'high' ? 'bg-danger/10 text-danger' :
                      task.priority === 'medium' ? 'bg-warning/10 text-warning' :
                      'bg-success/10 text-success'
                    }`}>
                      {task.priority}
                    </span>
                  </div>
                </Card>
              ))}
            </div>
          </div>
        ))}
      </div>

      {/* Upcoming Deadlines Timeline */}
      <Card className="p-6">
        <h3 className="text-lg font-semibold text-foreground mb-4">Upcoming Deadlines</h3>
        <div className="space-y-4">
          {['2024-11-20', '2024-11-22', '2024-11-25', '2024-12-01'].map((date) => (
            <div key={date} className="flex items-center gap-4">
              <div className="w-24 text-sm font-medium text-foreground">{date}</div>
              <div className="flex-1 h-2 bg-muted rounded-full overflow-hidden">
                <div className="h-full bg-primary w-1/3" />
              </div>
              <span className="text-sm text-muted-foreground">2 CAPAs</span>
            </div>
          ))}
        </div>
      </Card>
    </div>
  )
}
