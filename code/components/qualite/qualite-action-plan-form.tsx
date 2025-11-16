'use client'

import { useState } from 'react'
import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'
import { Trash2, Plus } from 'lucide-react'

interface Action {
  id: string
  title: string
  description: string
  owner: string
  startDate: string
  dueDate: string
  budget: string
}

export function QualiteActionPlanForm() {
  const [actions, setActions] = useState<Action[]>([
    {
      id: '1',
      title: 'Install preventive maintenance schedule',
      description: 'Implement automated maintenance checks',
      owner: 'John Smith',
      startDate: '2024-11-17',
      dueDate: '2024-12-01',
      budget: '5000',
    },
  ])

  const [expandedId, setExpandedId] = useState('1')
  const totalBudget = actions.reduce((sum, action) => sum + (parseFloat(action.budget) || 0), 0)

  const handleAddAction = () => {
    const newAction: Action = {
      id: Date.now().toString(),
      title: '',
      description: '',
      owner: '',
      startDate: '',
      dueDate: '',
      budget: '',
    }
    setActions([...actions, newAction])
    setExpandedId(newAction.id)
  }

  const handleUpdateAction = (id: string, field: keyof Action, value: string) => {
    setActions(actions.map((action) =>
      action.id === id ? { ...action, [field]: value } : action
    ))
  }

  const handleRemoveAction = (id: string) => {
    setActions(actions.filter((action) => action.id !== id))
  }

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-bold text-foreground">Action Plan</h2>
        <p className="text-muted-foreground">{actions.length} action(s)</p>
      </div>

      {/* Actions List */}
      <div className="space-y-3">
        {actions.map((action) => (
          <Card
            key={action.id}
            className="overflow-hidden cursor-pointer hover:shadow-md transition-shadow"
          >
            <button
              onClick={() => setExpandedId(expandedId === action.id ? '' : action.id)}
              className="w-full text-left p-4 hover:bg-muted/30 transition-colors"
            >
              <div className="flex items-center justify-between">
                <div className="flex-1">
                  <h3 className="font-semibold text-foreground">{action.title || 'New Action'}</h3>
                  <p className="text-sm text-muted-foreground mt-1">{action.description}</p>
                </div>
                <span className="text-muted-foreground">
                  {expandedId === action.id ? '▲' : '▼'}
                </span>
              </div>
            </button>

            {expandedId === action.id && (
              <div className="border-t p-4 bg-muted/10 space-y-4">
                <div>
                  <label className="block text-sm font-semibold text-foreground mb-2">
                    Action Title *
                  </label>
                  <input
                    type="text"
                    value={action.title}
                    onChange={(e) => handleUpdateAction(action.id, 'title', e.target.value)}
                    placeholder="What is the corrective action?"
                    className="w-full px-4 py-2 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
                  />
                </div>

                <div>
                  <label className="block text-sm font-semibold text-foreground mb-2">
                    Description
                  </label>
                  <textarea
                    value={action.description}
                    onChange={(e) => handleUpdateAction(action.id, 'description', e.target.value)}
                    placeholder="Use SMART framework: Specific, Measurable, Achievable, Relevant, Time-bound"
                    rows={3}
                    className="w-full px-4 py-2 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary resize-none"
                  />
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-semibold text-foreground mb-2">
                      Owner
                    </label>
                    <input
                      type="text"
                      value={action.owner}
                      onChange={(e) => handleUpdateAction(action.id, 'owner', e.target.value)}
                      placeholder="Responsible person"
                      className="w-full px-4 py-2 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-semibold text-foreground mb-2">
                      Budget ($)
                    </label>
                    <input
                      type="number"
                      value={action.budget}
                      onChange={(e) => handleUpdateAction(action.id, 'budget', e.target.value)}
                      placeholder="0"
                      className="w-full px-4 py-2 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
                    />
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-semibold text-foreground mb-2">
                      Start Date
                    </label>
                    <input
                      type="date"
                      value={action.startDate}
                      onChange={(e) => handleUpdateAction(action.id, 'startDate', e.target.value)}
                      className="w-full px-4 py-2 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-semibold text-foreground mb-2">
                      Due Date *
                    </label>
                    <input
                      type="date"
                      value={action.dueDate}
                      onChange={(e) => handleUpdateAction(action.id, 'dueDate', e.target.value)}
                      className="w-full px-4 py-2 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
                    />
                  </div>
                </div>

                <div className="flex justify-between pt-4 border-t">
                  <Button
                    onClick={() => handleRemoveAction(action.id)}
                    variant="outline"
                    className="text-danger hover:text-danger"
                  >
                    <Trash2 className="w-4 h-4 mr-2" />
                    Remove
                  </Button>
                </div>
              </div>
            )}
          </Card>
        ))}
      </div>

      {/* Add Action Button */}
      <Button
        onClick={handleAddAction}
        variant="outline"
        className="w-full text-primary border-primary hover:bg-primary/5"
      >
        <Plus className="w-4 h-4 mr-2" />
        Add New Action
      </Button>

      {/* Budget Summary */}
      <Card className="p-4 sticky bottom-0">
        <div className="flex items-center justify-between">
          <div>
            <p className="text-sm text-muted-foreground">Total Budget</p>
            <p className="text-2xl font-bold text-foreground">${totalBudget.toLocaleString()}</p>
          </div>
          <Button className="bg-primary text-primary-foreground hover:bg-primary/90">
            Save Action Plan
          </Button>
        </div>
      </Card>
    </div>
  )
}
