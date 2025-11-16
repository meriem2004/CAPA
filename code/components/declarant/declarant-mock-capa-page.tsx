'use client'

import { Button } from '@/components/ui/button'
import useCreateCapa from '@/components/declarant/services/hooks/useCreateCapa'
import useCapaList from '@/components/declarant/services/hooks/useCapaList'
import { useToast } from '@/hooks/use-toast'

function makeMockCapa() {
  const rand = Math.floor(Math.random() * 1000)
  const severities = ['minor', 'major', 'critical'] as const
  const severity = severities[Math.floor(Math.random() * severities.length)]
  // Ensure capaNumber length <= 20 to satisfy backend constraint
  const shortId = Math.random().toString(36).slice(2, 10) // 8 chars
  const capaNumber = `CAPA-${shortId}-${rand}`.slice(0, 20)
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
  }
}

export function DeclarantMockCapaPage() {
  const { toast } = useToast()
  const createCapa = useCreateCapa()
  const { data, isLoading, isError } = useCapaList()

  const onAddMock = () => {
    const payload = makeMockCapa()
    createCapa.mutate(payload, {
      onSuccess: () => {
        toast({ title: 'Mock CAPA created', description: `Created ${payload.capaNumber}` })
      },
      onError: (err: any) => {
        const message = err?.response?.data?.message || err?.message || 'Failed to create mock CAPA'
        toast({ title: 'Error', description: message })
      },
    })
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-bold text-foreground">Mock CAPA Creator</h2>
        <Button onClick={onAddMock} disabled={createCapa.isPending} className="px-6">
          {createCapa.isPending ? 'Creating...' : 'Add Mock CAPA'}
        </Button>
      </div>

      <div className="rounded-md border">
        <div className="grid grid-cols-4 gap-2 p-3 border-b text-sm font-semibold">
          <div>#</div>
          <div>Title</div>
          <div>Severity</div>
          <div>Status</div>
        </div>
        {isLoading && <div className="p-3 text-sm">Loading CAPA...</div>}
        {isError && <div className="p-3 text-sm text-danger">Failed to load CAPA list.</div>}
        {!isLoading && !isError && (
          <div className="divide-y">
            {data?.map((c: any) => (
              <div key={c.id} className="grid grid-cols-4 gap-2 p-3 text-sm">
                <div className="truncate">{c.capaNumber}</div>
                <div className="truncate">{c.title}</div>
                <div className="truncate capitalize">{c.severity}</div>
                <div className="truncate">{c.currentStatus}</div>
              </div>
            ))}
            {(!data || data.length === 0) && (
              <div className="p-3 text-sm text-muted-foreground">No CAPA found. Create one using the button above.</div>
            )}
          </div>
        )}
      </div>
    </div>
  )
}