'use client'

import { Card } from '@/components/ui/card'

interface ExecutiveMetric {
  label: string
  value: string
  change: string
  isPositive: boolean
}

const metrics: ExecutiveMetric[] = [
  { label: 'Budget Spent', value: '$45,230', change: '+12%', isPositive: false },
  { label: 'Pending Approvals', value: '8', change: '-25%', isPositive: true },
  { label: 'Team Efficiency', value: '87%', change: '+5%', isPositive: true },
  { label: 'On-Time CAPAs', value: '92%', change: '+8%', isPositive: true },
]

interface ApprovalItem {
  id: string
  capaNumber: string
  amount: string
  department: string
  status: 'pending' | 'approved' | 'rejected'
  priority: 'high' | 'medium' | 'low'
}

const approvalQueue: ApprovalItem[] = [
  { id: '1', capaNumber: 'CAPA-2024-001', amount: '$12,500', department: 'Manufacturing', status: 'pending', priority: 'high' },
  { id: '2', capaNumber: 'CAPA-2024-002', amount: '$8,300', department: 'Quality', status: 'pending', priority: 'high' },
  { id: '3', capaNumber: 'CAPA-2024-003', amount: '$5,200', department: 'Operations', status: 'pending', priority: 'medium' },
]

export function DirectionDashboard() {
  return (
    <div className="space-y-8">
      {/* Executive Summary */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {metrics.map((metric) => (
          <Card key={metric.label} className="p-4">
            <p className="text-sm text-muted-foreground font-semibold">{metric.label}</p>
            <div className="flex items-end justify-between mt-3">
              <p className="text-2xl font-bold text-foreground">{metric.value}</p>
              <span className={`text-sm font-semibold ${metric.isPositive ? 'text-success' : 'text-danger'}`}>
                {metric.change}
              </span>
            </div>
          </Card>
        ))}
      </div>

      {/* Approval Queue */}
      <div>
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-2xl font-bold text-foreground">Approval Queue</h2>
          <button className="text-primary hover:text-primary/80 font-medium text-sm">
            View All
          </button>
        </div>

        <Card>
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead className="border-b bg-muted/30">
                <tr>
                  <th className="text-left px-6 py-3 font-semibold text-foreground">CAPA</th>
                  <th className="text-left px-6 py-3 font-semibold text-foreground">Department</th>
                  <th className="text-left px-6 py-3 font-semibold text-foreground">Amount</th>
                  <th className="text-left px-6 py-3 font-semibold text-foreground">Priority</th>
                  <th className="text-left px-6 py-3 font-semibold text-foreground">Action</th>
                </tr>
              </thead>
              <tbody>
                {approvalQueue.map((item) => (
                  <tr key={item.id} className="border-b hover:bg-muted/20 transition-colors">
                    <td className="px-6 py-4">
                      <span className="font-semibold text-foreground">{item.capaNumber}</span>
                    </td>
                    <td className="px-6 py-4 text-muted-foreground">{item.department}</td>
                    <td className="px-6 py-4 font-medium text-foreground">{item.amount}</td>
                    <td className="px-6 py-4">
                      <span className={`px-3 py-1 rounded-full text-xs font-semibold ${
                        item.priority === 'high' ? 'bg-danger/10 text-danger' :
                        item.priority === 'medium' ? 'bg-warning/10 text-warning' :
                        'bg-success/10 text-success'
                      }`}>
                        {item.priority}
                      </span>
                    </td>
                    <td className="px-6 py-4">
                      <button className="text-primary hover:text-primary/80 font-medium text-sm">
                        Review
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </Card>
      </div>

      {/* CAPA Trends */}
      <Card className="p-6">
        <h3 className="text-lg font-semibold text-foreground mb-6">CAPA Trends - Last 30 Days</h3>
        <div className="grid grid-cols-4 gap-4">
          {['Created', 'Closed', 'Overdue', 'Pending'].map((label) => (
            <div key={label} className="space-y-3">
              <p className="text-xs text-muted-foreground font-semibold uppercase">{label}</p>
              <div className="flex flex-col items-start gap-2">
                {[25, 18, 12, 35].map((value, i) => (
                  <div key={i} className="w-full flex items-center gap-2">
                    <div className="flex-1 h-2 bg-muted rounded-full overflow-hidden">
                      <div
                        className={`h-full ${
                          label === 'Closed' ? 'bg-success' :
                          label === 'Overdue' ? 'bg-danger' :
                          label === 'Pending' ? 'bg-warning' :
                          'bg-primary'
                        }`}
                        style={{ width: `${(value / 35) * 100}%` }}
                      />
                    </div>
                    <span className="text-xs font-medium text-foreground w-8">{value}</span>
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>
      </Card>

      {/* Upcoming Deadlines */}
      <Card className="p-6">
        <h3 className="text-lg font-semibold text-foreground mb-4">Resource Commitments</h3>
        <div className="space-y-3">
          {['Finance Team', 'Engineering', 'Operations', 'Quality'].map((team) => (
            <div key={team} className="flex items-center justify-between">
              <span className="font-medium text-foreground">{team}</span>
              <div className="w-32 h-2 bg-muted rounded-full overflow-hidden">
                <div
                  className="h-full bg-primary"
                  style={{ width: `${Math.random() * 80 + 20}%` }}
                />
              </div>
            </div>
          ))}
        </div>
      </Card>
    </div>
  )
}
