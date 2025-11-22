'use client'

import { useEffect, useState } from 'react'
import { Card } from '@/components/ui/card'
import { Button } from '@/components/ui/button'

type DocumentItem = {
  id: number
  fileName: string
  s3Bucket: string | null
  s3Key: string
  fileSize: number | null
  uploadedAt: string
}

export function QualiteDocuments() {
  const [docs, setDocs] = useState<DocumentItem[]>([])
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [activeDoc, setActiveDoc] = useState<DocumentItem | null>(null)
  const [previewUrl, setPreviewUrl] = useState<string | null>(null)

  useEffect(() => {
    setIsLoading(true)
    fetch('http://localhost:8080/api/documents')
      .then((r) => r.json())
      .then((data) => setDocs(data))
      .catch(() => setError('Failed to load documents'))
      .finally(() => setIsLoading(false))
  }, [])

  const loadPreview = async (doc: DocumentItem) => {
    setActiveDoc(doc)
    setPreviewUrl(null)
    try {
      const res = await fetch(`http://localhost:8080/api/documents/${doc.id}/signed-url`)
      const json = await res.json()
      const url = json.url as string
      if (doc.fileName.toLowerCase().endsWith('.pdf')) {
        setPreviewUrl(url)
      } else {
        const office = `https://view.officeapps.live.com/op/view.aspx?src=${encodeURIComponent(url)}`
        setPreviewUrl(office)
      }
    } catch {
      setError('Failed to create preview')
    }
  }

  return (
    <div className="grid grid-cols-3 gap-6">
      <div className="col-span-1 space-y-4">
        <div className="flex items-center justify-between">
          <h2 className="text-2xl font-bold text-foreground">CAPA Documents</h2>
          <span className="text-muted-foreground text-sm">{docs.length}</span>
        </div>

        <Card className="overflow-hidden border">
          <div className="grid grid-cols-4 gap-2 p-3 border-b text-xs font-semibold">
            <div>Name</div>
            <div>Size</div>
            <div>Date</div>
            <div></div>
          </div>
          {isLoading && <div className="p-3 text-sm">Loading...</div>}
          {error && <div className="p-3 text-sm text-danger">{error}</div>}
          {!isLoading && !error && (
            <div className="divide-y">
              {docs.map((d) => (
                <div key={d.id} className="grid grid-cols-4 gap-2 p-3 text-sm items-center">
                  <div className="truncate" title={d.fileName}>{d.fileName}</div>
                  <div>{d.fileSize ? `${d.fileSize} B` : '—'}</div>
                  <div>{new Date(d.uploadedAt).toLocaleString()}</div>
                  <div className="flex gap-2 justify-end">
                    <Button size="sm" onClick={() => loadPreview(d)}>Preview</Button>
                    <a
                      href={`http://localhost:8080/api/documents/${d.id}/download`}
                      className="inline-flex h-9 items-center justify-center rounded-md border px-3 text-sm"
                    >Download</a>
                  </div>
                </div>
              ))}
              {docs.length === 0 && (
                <div className="p-3 text-sm text-muted-foreground">No documents</div>
              )}
            </div>
          )}
        </Card>
      </div>

      <div className="col-span-2">
        <Card className="p-3 h-[70vh]">
          {!activeDoc && <div className="text-sm text-muted-foreground">Select a document to preview</div>}
          {activeDoc && previewUrl && (
            activeDoc.fileName.toLowerCase().endsWith('.pdf') ? (
              <iframe src={previewUrl} className="w-full h-full" />
            ) : (
              <iframe src={previewUrl} className="w-full h-full" />
            )
          )}
        </Card>
      </div>
    </div>
  )
}

