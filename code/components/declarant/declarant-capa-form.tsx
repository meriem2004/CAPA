'use client'

import { useState } from 'react'
import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'
import { ChevronUp } from 'lucide-react'
import { useToast } from '@/hooks/use-toast'
import useCreateCapa from '@/components/declarant/services/hooks/useCreateCapa'
import type { CreateCapaRequest } from '@/components/declarant/services/api/capa.service'

export type CapaType = 'corrective' | 'preventive'
export type Severity = 'minor' | 'major' | 'critical'

export type CapaFormData = {
  title: string
  description?: string
  capaType: CapaType
  severity?: Severity
  dueDate?: string // ISO date string
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
    capaType: 'corrective',
    severity: 'minor',
    dueDate: '',
  })
  const [charCount, setCharCount] = useState(0)
  const maxChars = 1000
  const { toast } = useToast()
  const createCapa = useCreateCapa()

  const handleDescriptionChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    const text = e.target.value
    if (text.length <= maxChars) {
      setFormData({ ...formData, description: text })
      setCharCount(text.length)
    }
  }

  const progress = (charCount / maxChars) * 100

  const handleSubmit = async () => {
    if (!formData.title.trim()) {
      toast({
        title: 'Validation Error',
        description: 'Title is required',
        variant: 'destructive',
      })
      return
    }

    if (!formData.capaType) {
      toast({
        title: 'Validation Error',
        description: 'CAPA Type is required',
        variant: 'destructive',
      })
      return
    }

    // Prepare payload for API
    const payload: CreateCapaRequest = {
      title: formData.title.trim(),
      capaType: formData.capaType,
    }

    if (formData.description?.trim()) {
      payload.description = formData.description.trim()
    }

    if (formData.severity) {
      payload.severity = formData.severity
    }

    if (formData.dueDate) {
      // Convert date to ISO string format for LocalDateTime (YYYY-MM-DDTHH:mm:ss)
      // Set to end of day (23:59:59)
      const date = new Date(formData.dueDate)
      date.setHours(23, 59, 59, 0)
      // Format as YYYY-MM-DDTHH:mm:ss (without timezone for LocalDateTime)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      const seconds = String(date.getSeconds()).padStart(2, '0')
      payload.dueDate = `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`
    }

    try {
      await createCapa.mutateAsync(payload)
      toast({
        title: 'Success',
        description: 'CAPA created successfully',
      })
      onSubmit?.(formData)
      
      // Reset form
      setFormData({
        title: '',
        description: '',
        capaType: 'corrective',
        severity: 'minor',
        dueDate: '',
      })
      setCharCount(0)
    } catch (error: any) {
      const errorMessage = error?.response?.data?.message || error?.message || 'Failed to create CAPA'
      toast({
        title: 'Error',
        description: errorMessage,
        variant: 'destructive',
      })
    }
  }

  const handleSaveDraft = () => {
    onSaveDraft?.(formData)
    toast({
      title: 'Draft Saved',
      description: 'Your CAPA form has been saved as a draft',
    })
  }

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
            CAPA Title <span className="text-red-500">*</span>
          </label>
          <input
            type="text"
            value={formData.title}
            onChange={(e) => setFormData({ ...formData, title: e.target.value })}
            placeholder="Brief summary of the issue"
            className="w-full px-4 py-3 border border-border rounded-lg bg-background text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary"
            required
          />
        </div>

        <div>
          <label className="block text-sm font-semibold text-foreground mb-2">
            CAPA Type <span className="text-red-500">*</span>
          </label>
          <select
            value={formData.capaType}
            onChange={(e) => setFormData({ ...formData, capaType: e.target.value as CapaType })}
            className="w-full px-4 py-3 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
            required
          >
            <option value="corrective">Corrective</option>
            <option value="preventive">Preventive</option>
          </select>
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-semibold text-foreground mb-2">
              Due Date
            </label>
            <input
              type="date"
              value={formData.dueDate}
              onChange={(e) => setFormData({ ...formData, dueDate: e.target.value })}
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
              <option value="minor">Minor</option>
              <option value="major">Major</option>
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
            onClick={handleSaveDraft}
            disabled={createCapa.isPending}
          >
            Save Draft
          </Button>
          <Button
            className="px-6 py-2 bg-primary text-primary-foreground hover:bg-primary/90"
            onClick={handleSubmit}
            disabled={createCapa.isPending || !formData.title.trim()}
          >
            {createCapa.isPending ? 'Submitting...' : 'Submit CAPA'}
          </Button>
        </div>
      </div>

      <div className="h-24" />
    </div>
  )
}
