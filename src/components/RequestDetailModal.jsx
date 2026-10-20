// ============================================================
// BLOODLINK — REQUEST DETAIL MODAL
// ============================================================
import { useState } from 'react';
import { X, AlertCircle, MapPin, Clock, Edit2, CheckCircle2, XCircle, RotateCcw } from 'lucide-react';
import { useApp } from '../services/AppStore';
import { useToast } from '../hooks/useToast';
import { URGENCY_CONFIG, STATUS_CONFIG, timeAgo, formatDateTime } from '../utils/helpers';
import { REQUEST_STATUSES } from '../data/seedData';

const STATUS_LABELS = {
  pending: 'Pending', reviewing: 'Reviewing',
  in_progress: 'In Progress', fulfilled: 'Fulfilled', cancelled: 'Cancelled',
};
const STATUS_FLOW = ['pending', 'reviewing', 'in_progress', 'fulfilled'];

export default function RequestDetailModal({ request, onClose }) {
  const { actions } = useApp();
  const { toastSuccess } = useToast();
  const [confirmCancel, setConfirmCancel] = useState(false);

  const urgency = URGENCY_CONFIG[request.urgency] || URGENCY_CONFIG.normal;
  const status = STATUS_CONFIG[request.status] || STATUS_CONFIG.pending;

  const handleStatusChange = (newStatus) => {
    actions.updateRequest(request.id, { status: newStatus });
    toastSuccess('Status updated', `${request.id} is now ${STATUS_LABELS[newStatus]}.`);
    onClose();
  };

  const handleCancel = () => {
    actions.updateRequest(request.id, { status: 'cancelled' });
    toastSuccess('Request cancelled', `${request.id} has been cancelled.`);
    onClose();
  };

  const isActive = !['fulfilled', 'cancelled'].includes(request.status);
  const currentStep = STATUS_FLOW.indexOf(request.status);
  const nextStatus = STATUS_FLOW[currentStep + 1];

  return (
    <div className="modal-overlay" role="dialog" aria-modal="true" aria-labelledby="req-detail-title">
      <div className="modal" style={{ maxWidth: 600 }}>
        <div className="modal-header">
          <div>
            <div className="flex items-center gap-2" style={{ marginBottom: 4 }}>
              <span className={`priority-dot ${urgency.dotClass}`} aria-hidden="true" />
              <span style={{ fontSize: 'var(--text-xs)', fontWeight: 600, color: 'var(--text-muted)', letterSpacing: 'var(--tracking-wide)', textTransform: 'uppercase' }}>
                {urgency.label} Priority
              </span>
            </div>
            <h2 id="req-detail-title" className="modal-title">{request.id}</h2>
            <p style={{ fontSize: 'var(--text-xs)', color: 'var(--text-muted)', marginTop: 2 }}>{request.hospital}</p>
          </div>
          <button className="btn btn-icon btn-ghost" onClick={onClose} aria-label="Close"><X size={16} /></button>
        </div>

        <div className="modal-body">
          {/* Key info row */}
          <div className="req-detail-grid">
            <div className="req-detail-block">
              <span className="donor-detail-label">Blood Group</span>
              <span className="blood-badge" style={{ fontSize: 'var(--text-base)', minWidth: 48, height: 28 }}>{request.bloodGroup}</span>
            </div>
            <div className="req-detail-block">
              <span className="donor-detail-label">Units Required</span>
              <span style={{ fontSize: 'var(--text-xl)', fontWeight: 700, color: 'var(--text-primary)', lineHeight: 1 }}>{request.units}</span>
            </div>
            <div className="req-detail-block">
              <span className="donor-detail-label">Status</span>
              <span className={`badge ${status.badgeClass}`}>{status.label}</span>
            </div>
            <div className="req-detail-block">
              <span className="donor-detail-label">Location</span>
              <span className="flex items-center gap-1" style={{ fontSize: 'var(--text-sm)', color: 'var(--text-secondary)', fontWeight: 500 }}>
                <MapPin size={12} aria-hidden="true" />{request.location}
              </span>
            </div>
            <div className="req-detail-block">
              <span className="donor-detail-label">Submitted</span>
              <span style={{ fontSize: 'var(--text-sm)', color: 'var(--text-secondary)' }}>{formatDateTime(request.createdAt)}</span>
            </div>
            <div className="req-detail-block">
              <span className="donor-detail-label">Requester</span>
              <span style={{ fontSize: 'var(--text-sm)', color: 'var(--text-secondary)' }}>{request.requester || '—'}</span>
            </div>
          </div>

          {request.notes && (
            <div className="req-notes-block">
              <p className="donor-detail-label" style={{ marginBottom: 'var(--space-1)' }}>Notes</p>
              <p style={{ fontSize: 'var(--text-sm)', color: 'var(--text-secondary)', lineHeight: 'var(--leading-relaxed)' }}>{request.notes}</p>
            </div>
          )}

          {/* Progress timeline */}
          {!['cancelled'].includes(request.status) && (
            <div className="req-timeline">
              <p className="donor-detail-label" style={{ marginBottom: 'var(--space-3)' }}>Progress</p>
              <div className="req-timeline-row">
                {STATUS_FLOW.map((s, i) => {
                  const past = STATUS_FLOW.indexOf(request.status) >= i;
                  const current = request.status === s;
                  return (
                    <div key={s} className="req-timeline-step">
                      <div className={`req-timeline-dot${past ? ' past' : ''}${current ? ' current' : ''}`} />
                      <span className={`req-timeline-label${current ? ' current-label' : ''}`}>{STATUS_LABELS[s]}</span>
                      {i < STATUS_FLOW.length - 1 && <div className={`req-timeline-connector${past && i < STATUS_FLOW.indexOf(request.status) ? ' filled' : ''}`} />}
                    </div>
                  );
                })}
              </div>
            </div>
          )}

          {/* Demo notice */}
          <p style={{ fontSize: 'var(--text-xs)', color: 'var(--text-muted)', lineHeight: 'var(--leading-relaxed)' }}>
            This is a demo request. No external parties have been notified. Status changes are local only.
          </p>

          {/* Confirm cancel */}
          {confirmCancel && (
            <div style={{
              padding: 'var(--space-4)',
              background: 'var(--crimson-surface)',
              border: '1px solid var(--crimson-muted)',
              borderRadius: 'var(--radius-lg)',
            }}>
              <p style={{ fontSize: 'var(--text-sm)', fontWeight: 600, color: 'var(--crimson-light)', marginBottom: 'var(--space-2)' }}>
                Cancel this request?
              </p>
              <p style={{ fontSize: 'var(--text-xs)', color: 'var(--text-secondary)', marginBottom: 'var(--space-3)', lineHeight: 'var(--leading-relaxed)' }}>
                The demo request will be marked as Cancelled. This cannot be undone in this session.
              </p>
              <div className="flex gap-2">
                <button className="btn btn-secondary btn-sm" onClick={() => setConfirmCancel(false)}>Keep Active</button>
                <button className="btn btn-danger btn-sm" onClick={handleCancel}>
                  <XCircle size={13} /> Confirm Cancellation
                </button>
              </div>
            </div>
          )}
        </div>

        <div className="modal-footer" style={{ justifyContent: 'space-between' }}>
          <div>
            {isActive && !confirmCancel && (
              <button className="btn btn-ghost btn-sm" style={{ color: 'var(--crimson-light)' }} onClick={() => setConfirmCancel(true)}>
                <XCircle size={13} /> Cancel Request
              </button>
            )}
          </div>
          <div className="flex gap-2">
            <button className="btn btn-secondary" onClick={onClose}>Close</button>
            {isActive && nextStatus && (
              <button className="btn btn-primary" onClick={() => handleStatusChange(nextStatus)}>
                <CheckCircle2 size={14} aria-hidden="true" />
                Mark as {STATUS_LABELS[nextStatus]}
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
