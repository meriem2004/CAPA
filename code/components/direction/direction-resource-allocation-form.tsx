'use client'

import { useState } from 'react'
import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'

interface ResourceAction {
  id: string
  title: string
  requiredBudget: string
  allocatedBudget: string
  personnel: string
  equipment: string
  fundingSource: string
}

export function DirectionResourceAllocationForm() {
  const [actions, setActions] = useState<ResourceAction[]>([
    {
      id: '1',
      title: 'Install preventive maintenance',
      requiredBudget: '15000',
      allocatedBudget: '12500',
      personnel: 'Technician A (40h), Technician B (30h)',
      equipment: 'Diagnostic tool, Installation kit',
      fundingSource: 'CAPEX-2024',
    },
  ])

  const totalRequired = actions.reduce((sum, a) => sum + (parseFloat(a.requiredBudget) || 0), 0)
  const totalAllocated = actions.reduce((sum, a) => sum + (parseFloat(a.allocatedBudget) || 0), 0)
  const fundingGap = totalRequired - totalAllocated

  return (
    <div className="max-w-5xl mx-auto space-y-6">
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-bold text-foreground">Resource Allocation</h2>
        <p className="text-muted-foreground">{actions.length} action(s)</p>
      </div>

      {/* Resource Availability */}
      <div className="grid grid-cols-3 gap-4">
        <Card className="p-4">
          <p className="text-sm text-muted-foreground mb-2">Resource Availability</p>
          <div className="flex items-center gap-3">
            <div className="w-4 h-4 rounded-full bg-success"></div>
            <span className="font-semibold text-foreground">Adequate</span>
          </div>
        </Card>
        <Card className="p-4">
          <p className="text-sm text-muted-foreground mb-2">Budget Status</p>
          <div className="flex items-center gap-3">
            <div className="w-4 h-4 rounded-full bg-warning"></div>
            <span className="font-semibold text-foreground">Constrained</span>
          </div>
        </Card>
        <Card className="p-4">
          <p className="text-sm text-muted-foreground mb-2">Timeline</p>
          <div className="flex items-center gap-3">
            <div className="w-4 h-4 rounded-full bg-success"></div>
            <span className="font-semibold text-foreground">On Track</span>
          </div>
        </Card>
      </div>

      {/* Actions Grid */}
      <div className="grid grid-cols-3 gap-4">
        {actions.map((action) => (
          <Card key={action.id} className="p-4 border-2 border-primary/30 hover:border-primary/60 transition-colors">
            <h3 className="font-semibold text-foreground mb-4 line-clamp-2">{action.title}</h3>
            <div className="space-y-3 text-sm">
              <div>
                <p className="text-muted-foreground text-xs mb-1">Requested Budget</p>
                <p className="font-bold text-foreground">${parseFloat(action.requiredBudget).toLocaleString()}</p>
              </div>
              <div>
                <p className="text-muted-foreground text-xs mb-1">Allocated Budget</p>
                <p className="font-bold text-success">${parseFloat(action.allocatedBudget).toLocaleString()}</p>
              </div>
              <div>
                <p className="text-muted-foreground text-xs mb-1">Personnel</p>
                <p className="text-foreground text-xs">{action.personnel}</p>
              </div>
              <div>
                <p className="text-muted-foreground text-xs mb-1">Equipment</p>
                <p className="text-foreground text-xs">{action.equipment}</p>
              </div>
              <div>
                <p className="text-muted-foreground text-xs mb-1">Funding Source</p>
                <p className="font-medium text-foreground">{action.fundingSource}</p>
              </div>
            </div>
          </Card>
        ))}
      </div>

      {/* Budget Comparison */}
      <Card className="p-6">
        <h3 className="font-semibold text-foreground mb-6">Budget Summary</h3>
        <div className="grid grid-cols-3 gap-6">
          <div>
            <p className="text-sm text-muted-foreground mb-2">Total Requested</p>
            <p className="text-3xl font-bold text-foreground">${totalRequired.toLocaleString()}</p>
          </div>
          <div>
            <p className="text-sm text-muted-foreground mb-2">Total Allocated</p>
            <p className="text-3xl font-bold text-success">${totalAllocated.toLocaleString()}</p>
          </div>
          <div>
            <p className="text-sm text-muted-foreground mb-2">Funding Gap</p>
            <p className={`text-3xl font-bold ${fundingGap > 0 ? 'text-danger' : 'text-success'}`}>
              ${Math.abs(fundingGap).toLocaleString()}
            </p>
          </div>
        </div>

        <div className="mt-6 pt-6 border-t space-y-4">
          <div>
            <div className="flex justify-between items-center mb-2">
              <p className="text-sm font-medium text-foreground">Budget Allocation %</p>
              <p className="text-sm font-semibold text-foreground">{((totalAllocated / totalRequired) * 100).toFixed(1)}%</p>
            </div>
            <div className="w-full h-3 bg-muted rounded-full overflow-hidden">
              <div
                className="h-full bg-primary transition-all"
                style={{ width: `${(totalAllocated / totalRequired) * 100}%` }}
              />
            </div>
          </div>
        </div>
      </Card>

      {/* Action Buttons */}
      <div className="sticky bottom-0 bg-background border-t p-4 -mx-4 px-4">
        <div className="flex gap-3 justify-end">
          <Button variant="outline">Cancel</Button>
          <Button className="bg-primary text-primary-foreground hover:bg-primary/90">
            Approve & Allocate Resources
          </Button>
        </div>
      </div>
    </div>
  )
}
