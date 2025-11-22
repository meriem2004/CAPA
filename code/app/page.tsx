'use client'

import { useState } from 'react'
import { LoginPage } from '@/components/login-page'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { DeclarantDashboard } from '@/components/declarant/declarant-dashboard'
import { QualiteDashboard } from '@/components/qualite/qualite-dashboard'
import { DirectionDashboard } from '@/components/direction/direction-dashboard'
import { DeclarantCapaForm } from '@/components/declarant/declarant-capa-form'
import { QualiteRcaForm } from '@/components/qualite/qualite-rca-form'
import { QualiteActionPlanForm } from '@/components/qualite/qualite-action-plan-form'
import { QualiteRiskAssessmentForm } from '@/components/qualite/qualite-risk-assessment-form'
import { QualiteDocuments } from '@/components/qualite/qualite-documents'
import { DirectionResourceAllocationForm } from '@/components/direction/direction-resource-allocation-form'
import { DirectionPlanValidationForm } from '@/components/direction/direction-plan-validation-form'
import { Button } from '@/components/ui/button'
import { LogOut } from 'lucide-react'
import { listCapas, createCapa } from '@/components/declarant/services/api/capa.service'
import { ping } from '@/components/declarant/services/api/tasks.service'
import { DeclarantMockCapaPage } from '@/components/declarant/declarant-mock-capa-page'

export default function Home() {
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  const [userRole, setUserRole] = useState<'declarant' | 'qualite' | 'direction' | null>(null)
  
  const [activeRole, setActiveRole] = useState('declarant')
  const [activeView, setActiveView] = useState('dashboard')

  const handleLogin = (role: 'declarant' | 'qualite' | 'direction') => {
    setIsAuthenticated(true)
    setUserRole(role)
    setActiveRole(role)
  }

  // Example usage to ensure services can be called (not rendered)
  // You can remove this later or wire it into components
  // void ping().then(console.log).catch(console.error)

  const handleLogout = () => {
    setIsAuthenticated(false)
    setUserRole(null)
    setActiveRole('declarant')
    setActiveView('dashboard')
  }

  if (!isAuthenticated) {
    return <LoginPage onLogin={handleLogin} />
  }

  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
      <div className="border-b bg-card sticky top-0 z-50">
        <div className="container mx-auto px-4 py-4">
          <div className="flex items-center justify-between mb-4">
            <h1 className="text-3xl font-bold text-foreground">CAPA Management System</h1>
            <Button
              onClick={handleLogout}
              variant="outline"
              size="sm"
              className="flex items-center gap-2"
            >
              <LogOut className="w-4 h-4" />
              Logout
            </Button>
          </div>
          <Tabs value={activeRole} onValueChange={(value) => {
            setActiveRole(value as 'declarant' | 'qualite' | 'direction')
            setActiveView('dashboard')
          }} className="w-full">
            
          </Tabs>
        </div>
      </div>

      <div className="container mx-auto px-4 py-8">
        {activeRole === 'declarant' && (
          <div className="space-y-4">
            <div className="flex gap-2 border-b">
              <button
                onClick={() => setActiveView('dashboard')}
                className={`px-4 py-2 font-medium border-b-2 ${
                  activeView === 'dashboard'
                    ? 'border-primary text-primary'
                    : 'border-transparent text-muted-foreground'
                }`}
              >
                Dashboard
              </button>
              <button
                onClick={() => setActiveView('capa-form')}
                className={`px-4 py-2 font-medium border-b-2 ${
                  activeView === 'capa-form'
                    ? 'border-primary text-primary'
                    : 'border-transparent text-muted-foreground'
                }`}
              >
                Open CAPA Form
              </button>
              <button
                onClick={() => setActiveView('mock-capa')}
                className={`px-4 py-2 font-medium border-b-2 ${
                  activeView === 'mock-capa'
                    ? 'border-primary text-primary'
                    : 'border-transparent text-muted-foreground'
                }`}
              >
                Mock CAPA
              </button>
            </div>
            {activeView === 'dashboard' && <DeclarantDashboard />}
            {activeView === 'capa-form' && <DeclarantCapaForm />}
            {/* Mock CAPA page similar to old frontend */}
            {activeView === 'mock-capa' && <DeclarantMockCapaPage />}
          </div>
        )}

        {activeRole === 'qualite' && (
          <div className="space-y-4">
            <div className="flex gap-2 border-b overflow-x-auto">
              <button
                onClick={() => setActiveView('dashboard')}
                className={`px-4 py-2 font-medium border-b-2 whitespace-nowrap ${
                  activeView === 'dashboard'
                    ? 'border-primary text-primary'
                    : 'border-transparent text-muted-foreground'
                }`}
              >
                Dashboard
              </button>
              <button
                onClick={() => setActiveView('rca')}
                className={`px-4 py-2 font-medium border-b-2 whitespace-nowrap ${
                  activeView === 'rca'
                    ? 'border-primary text-primary'
                    : 'border-transparent text-muted-foreground'
                }`}
              >
                RCA Form
              </button>
              <button
                onClick={() => setActiveView('action-plan')}
                className={`px-4 py-2 font-medium border-b-2 whitespace-nowrap ${
                  activeView === 'action-plan'
                    ? 'border-primary text-primary'
                    : 'border-transparent text-muted-foreground'
                }`}
              >
                Action Plan
              </button>
              <button
                onClick={() => setActiveView('risk')}
                className={`px-4 py-2 font-medium border-b-2 whitespace-nowrap ${
                  activeView === 'risk'
                    ? 'border-primary text-primary'
                    : 'border-transparent text-muted-foreground'
                }`}
              >
                Risk Assessment
              </button>
              <button
                onClick={() => setActiveView('documents')}
                className={`px-4 py-2 font-medium border-b-2 whitespace-nowrap ${
                  activeView === 'documents'
                    ? 'border-primary text-primary'
                    : 'border-transparent text-muted-foreground'
                }`}
              >
                Documents
              </button>
            </div>
            {activeView === 'dashboard' && <QualiteDashboard />}
            {activeView === 'rca' && <QualiteRcaForm />}
            {activeView === 'action-plan' && <QualiteActionPlanForm />}
            {activeView === 'risk' && <QualiteRiskAssessmentForm />}
            {activeView === 'documents' && <QualiteDocuments />}
          </div>
        )}

        {activeRole === 'direction' && (
          <div className="space-y-4">
            <div className="flex gap-2 border-b">
              <button
                onClick={() => setActiveView('dashboard')}
                className={`px-4 py-2 font-medium border-b-2 ${
                  activeView === 'dashboard'
                    ? 'border-primary text-primary'
                    : 'border-transparent text-muted-foreground'
                }`}
              >
                Dashboard
              </button>
              <button
                onClick={() => setActiveView('resource-allocation')}
                className={`px-4 py-2 font-medium border-b-2 whitespace-nowrap ${
                  activeView === 'resource-allocation'
                    ? 'border-primary text-primary'
                    : 'border-transparent text-muted-foreground'
                }`}
              >
                Resource Allocation
              </button>
              <button
                onClick={() => setActiveView('plan-validation')}
                className={`px-4 py-2 font-medium border-b-2 whitespace-nowrap ${
                  activeView === 'plan-validation'
                    ? 'border-primary text-primary'
                    : 'border-transparent text-muted-foreground'
                }`}
              >
                Plan Validation
              </button>
            </div>
            {activeView === 'dashboard' && <DirectionDashboard />}
            {activeView === 'resource-allocation' && <DirectionResourceAllocationForm />}
            {activeView === 'plan-validation' && <DirectionPlanValidationForm />}
          </div>
        )}
      </div>
    </div>
  )
}
