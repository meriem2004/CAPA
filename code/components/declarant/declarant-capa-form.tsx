'use client'

import { useState } from 'react'
import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'
import { ChevronUp } from 'lucide-react'

type Severity = 'low' | 'medium' | 'high' | 'critical'

export type CapaFormData = {
  title: string
  description: string
  department: string
  eventDate: string
  severity: Severity
}

export function DeclarantCapaForm({
  onSubmit,
  onSaveDraft,
}: {
  onSubmit?: (data: CapaFormData) => void
  onSaveDraft?: (data: CapaFormData) => void
}) {
  const [isContextOpen, setIsContextOpen] = useState(true)
  const [formData, setFormData] = useState<CapaFormData>({
    title: '',
    description: '',
    department: '',
    eventDate: '',
    severity: 'medium',
  })
  const [charCount, setCharCount] = useState(0)
  const maxChars = 1000

  const handleDescriptionChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    const text = e.target.value
    if (text.length <= maxChars) {
      setFormData({ ...formData, description: text })
      setCharCount(text.length)
    }
  }

  const progress = (charCount / maxChars) * 100

  return (
    <div className="max-w-2xl mx-auto space-y-6">
      <div className="sticky top-0 z-10 bg-background border-b py-4 -mx-4 px-4">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-2xl font-bold text-foreground">Open CAPA Form</h2>
            <p className="text-sm text-muted-foreground mt-1">Event detected - Please provide details</p>
          </div>
          <div className="text-right">
            <p className="text-2xl font-bold text-primary">42%</p>
            <p className="text-xs text-muted-foreground">Completion</p>
          </div>
        </div>
      </div>

      {/* Context Section */}
      <Card className="bg-muted/30 border border-muted">
        <button
          onClick={() => setIsContextOpen(!isContextOpen)}
          className="w-full flex items-center justify-between p-4 hover:bg-muted/40 transition-colors"
        >
          <h3 className="font-semibold text-foreground">Event Context</h3>
          <ChevronUp className={`${!isContextOpen ? 'rotate-180' : ''} w-5 h-5 transition-transform`} />
        </button>
        {isContextOpen && (
          <div className="px-4 pb-4 space-y-2 border-t border-muted">
            <p className="text-sm"><span className="font-semibold">Event Type:</span> Quality Deviation</p>
            <p className="text-sm"><span className="font-semibold">Detected:</span> 2024-11-16 14:30:00</p>
            <p className="text-sm"><span className="font-semibold">Location:</span> Production Line A</p>
          </div>
        )}
      </Card>

      {/* Form Fields */}
      <div className="space-y-6">
        <div>
          <label className="block text-sm font-semibold text-foreground mb-2">
            CAPA Title
          </label>
          <input
            type="text"
            value={formData.title}
            onChange={(e) => setFormData({ ...formData, title: e.target.value })}
            placeholder="Brief summary of the issue"
            className="w-full px-4 py-3 border border-border rounded-lg bg-background text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary"
          />
        </div>

        <div>
          <label className="block text-sm font-semibold text-foreground mb-2">
            Department
          </label>
          <select
            value={formData.department}
            onChange={(e) => setFormData({ ...formData, department: e.target.value })}
            className="w-full px-4 py-3 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
          >
            <option value="">Select a department</option>
            <option value="manufacturing">Manufacturing</option>
            <option value="quality">Quality</option>
            <option value="operations">Operations</option>
            <option value="maintenance">Maintenance</option>
          </select>
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-semibold text-foreground mb-2">
              Event Date
            </label>
            <input
              type="date"
              value={formData.eventDate}
              onChange={(e) => setFormData({ ...formData, eventDate: e.target.value })}
              className="w-full px-4 py-3 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
            />
          </div>
          <div>
            <label className="block text-sm font-semibold text-foreground mb-2">
              Severity
            </label>
            <select
              value={formData.severity}
              onChange={(e) => setFormData({ ...formData, severity: e.target.value as Severity })}
              className="w-full px-4 py-3 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
            >
              <option value="low">Low</option>
              <option value="medium">Medium</option>
              <option value="high">High</option>
              <option value="critical">Critical</option>
            </select>
          </div>
        </div>

        <div>
          <label className="block text-sm font-semibold text-foreground mb-2">
            Description
          </label>
          <textarea
            value={formData.description}
            onChange={handleDescriptionChange}
            placeholder="Describe the issue in detail..."
            rows={6}
            className="w-full px-4 py-3 border border-border rounded-lg bg-background text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary resize-none"
          />
          <div className="mt-2 flex items-center justify-between">
            <p className="text-xs text-muted-foreground">
              {charCount} / {maxChars} characters
            </p>
            <div className="w-32 h-1 bg-muted rounded-full overflow-hidden">
              <div
                className="h-full bg-primary transition-all"
                style={{ width: `${progress}%` }}
              />
            </div>
          </div>
        </div>
      </div>

      {/* Action Buttons */}
      <div className="fixed bottom-0 left-0 right-0 bg-background border-t p-4">
        <div className="container mx-auto max-w-2xl flex gap-4 justify-end">
          <Button
            variant="outline"
            className="px-6 py-2"
            onClick={() => onSaveDraft?.(formData)}
          >
            Save Draft
          </Button>
          <Button
            className="px-6 py-2 bg-primary text-primary-foreground hover:bg-primary/90"
            onClick={() => onSubmit?.(formData)}
          >
            Submit CAPA
          </Button>
        </div>
      </div>

      <div className="h-24" />
    </div>
  )
}
