// ============================================================
// BLOODLINK — DONOR DETAIL MODAL
// ============================================================
import { useState } from 'react';
import { X, Edit2, Trash2, Phone, MapPin, Calendar, Clock, Info } from 'lucide-react';
import { useApp } from '../services/AppStore';
import { useToast } from '../hooks/useToast';
import { AVAILABILITY_CONFIG, formatDate } from '../utils/helpers';
import { BLOOD_GROUPS as BG_LIST } from '../data/seedData';

export default function DonorDetailModal({ donor, onClose }) {
  const { actions } = useApp();
  const { toastSuccess, toastError } = useToast();
  const [mode, setMode] = useState('view'); // 'view' | 'edit' | 'confirm-delete'
  const [editForm, setEditForm] = useState({ ...donor });
  const [saving, setSaving] = useState(false);

  const avail = AVAILABILITY_CONFIG[donor.availability] || AVAILABILITY_CONFIG.unavailable;

  const handleSave = () => {
    if (!editForm.name.trim() || !editForm.bloodGroup || !editForm.city.trim()) {
      toastError('Validation', 'Name, blood group, and city are required.');
      return;
    }
    setSaving(true);
    try {
      actions.updateDonor(donor.id, editForm);
      toastSuccess('Donor updated', `${editForm.name}'s demo record has been updated.`);
      onClose();
    } catch {
      toastError('Error', 'Failed to update donor.');
      setSaving(false);
    }
  };

  const handleDelete = () => {
    actions.deleteDonor(donor.id);
    toastSuccess('Donor removed', `${donor.name} has been removed from the demo registry.`);
    onClose();
  };

  return (
    <div className="modal-overlay" role="dialog" aria-modal="true" aria-labelledby="donor-detail-title">
      <div className="modal">
        <div className="modal-header">
          <div>
            <div className="flex items-center gap-2">
              <span className="blood-badge">{donor.bloodGroup}</span>
              <h2 id="donor-detail-title" className="modal-title">{donor.name}</h2>
            </div>
            <p style={{ fontSize: 'var(--text-xs)', color: 'var(--text-muted)', marginTop: 2 }}>{donor.id} · Demo Record</p>
          </div>
          <button className="btn btn-icon btn-ghost" onClick={onClose} aria-label="Close"><X size={16} /></button>
        </div>

        {mode === 'view' && (
          <>
            <div className="modal-body">
              <div className="donor-detail-grid">
                <div className="donor-detail-field">
                  <span className="donor-detail-label">Blood Group</span>
                  <span className="blood-badge" style={{ width: 'fit-content', fontSize: 'var(--text-sm)' }}>{donor.bloodGroup}</span>
                </div>
                <div className="donor-detail-field">
                  <span className="donor-detail-label">Demo Availability</span>
                  <span className={`badge ${avail.badgeClass}`}>{avail.label}</span>
                </div>
                <div className="donor-detail-field">
                  <span className="donor-detail-label"><MapPin size={10} style={{ display: 'inline', verticalAlign: 'middle' }} /> City</span>
                  <span className="donor-detail-value">{donor.city}</span>
                </div>
                <div className="donor-detail-field">
                  <span className="donor-detail-label"><Phone size={10} style={{ display: 'inline', verticalAlign: 'middle' }} /> Contact (Demo)</span>
                  <span className="donor-detail-value">{donor.phone || '—'}</span>
                </div>
                <div className="donor-detail-field">
                  <span className="donor-detail-label"><Calendar size={10} style={{ display: 'inline', verticalAlign: 'middle' }} /> Last Donation</span>
                  <span className="donor-detail-value">{donor.lastDonation ? formatDate(donor.lastDonation) : '—'}</span>
                </div>
                <div className="donor-detail-field">
                  <span className="donor-detail-label">Eligible After</span>
                  <span className="donor-detail-value">{donor.eligibleAfter ? formatDate(donor.eligibleAfter) : '—'}</span>
                </div>
                <div className="donor-detail-field">
                  <span className="donor-detail-label">Registered</span>
                  <span className="donor-detail-value">{donor.registered ? formatDate(donor.registered) : '—'}</span>
                </div>
              </div>

              <div style={{
                padding: 'var(--space-3)',
                background: 'var(--amber-surface)',
                border: '1px solid var(--amber-muted)',
                borderRadius: 'var(--radius-md)',
                fontSize: 'var(--text-xs)',
                color: 'var(--text-secondary)',
                lineHeight: 'var(--leading-relaxed)',
              }}>
                <strong style={{ color: 'var(--amber)' }}>Demo Notice:</strong> Availability status is for demonstration only. BloodLink does not verify medical eligibility. Contact information is fictional.
              </div>
            </div>
            <div className="modal-footer" style={{ justifyContent: 'space-between' }}>
              <button className="btn btn-danger btn-sm" onClick={() => setMode('confirm-delete')}>
                <Trash2 size={13} /> Remove
              </button>
              <div className="flex gap-2">
                <button className="btn btn-secondary" onClick={onClose}>Close</button>
                <button className="btn btn-primary" onClick={() => setMode('edit')}>
                  <Edit2 size={14} /> Edit
                </button>
              </div>
            </div>
          </>
        )}

        {mode === 'edit' && (
          <>
            <div className="modal-body">
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 'var(--space-4)' }}>
                <div className="form-group" style={{ gridColumn: '1 / -1' }}>
                  <label className="form-label form-label-required" htmlFor="edit-name">Full Name</label>
                  <input id="edit-name" type="text" className="form-input" value={editForm.name}
                    onChange={e => setEditForm(f => ({ ...f, name: e.target.value }))} />
                </div>
                <div className="form-group">
                  <label className="form-label form-label-required" htmlFor="edit-bg">Blood Group</label>
                  <select id="edit-bg" className="form-select" value={editForm.bloodGroup}
                    onChange={e => setEditForm(f => ({ ...f, bloodGroup: e.target.value }))}>
                    {BG_LIST.map(g => <option key={g} value={g}>{g}</option>)}
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label form-label-required" htmlFor="edit-city">City</label>
                  <input id="edit-city" type="text" className="form-input" value={editForm.city}
                    onChange={e => setEditForm(f => ({ ...f, city: e.target.value }))} />
                </div>
                <div className="form-group">
                  <label className="form-label" htmlFor="edit-phone">Phone (Demo)</label>
                  <input id="edit-phone" type="tel" className="form-input" value={editForm.phone}
                    onChange={e => setEditForm(f => ({ ...f, phone: e.target.value }))} />
                </div>
                <div className="form-group">
                  <label className="form-label" htmlFor="edit-avail">Demo Availability</label>
                  <select id="edit-avail" className="form-select" value={editForm.availability}
                    onChange={e => setEditForm(f => ({ ...f, availability: e.target.value }))}>
                    <option value="available">Available</option>
                    <option value="unavailable">Unavailable</option>
                    <option value="on_leave">On Leave</option>
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label" htmlFor="edit-lastDonation">Last Donation</label>
                  <input id="edit-lastDonation" type="date" className="form-input" value={editForm.lastDonation}
                    max={new Date().toISOString().split('T')[0]}
                    onChange={e => setEditForm(f => ({ ...f, lastDonation: e.target.value }))} />
                </div>
              </div>
            </div>
            <div className="modal-footer">
              <button className="btn btn-secondary" onClick={() => setMode('view')}>Cancel</button>
              <button className="btn btn-primary" onClick={handleSave} disabled={saving}>
                {saving ? <span className="spinner" aria-hidden="true" /> : null}
                Save Changes
              </button>
            </div>
          </>
        )}

        {mode === 'confirm-delete' && (
          <>
            <div className="modal-body">
              <div style={{
                padding: 'var(--space-5)',
                background: 'var(--crimson-surface)',
                border: '1px solid var(--crimson-muted)',
                borderRadius: 'var(--radius-lg)',
                textAlign: 'center',
              }}>
                <Trash2 size={24} color="var(--crimson-light)" style={{ margin: '0 auto var(--space-3)' }} aria-hidden="true" />
                <p style={{ fontSize: 'var(--text-base)', fontWeight: 600, color: 'var(--text-primary)', marginBottom: 'var(--space-2)' }}>
                  Remove {donor.name}?
                </p>
                <p style={{ fontSize: 'var(--text-sm)', color: 'var(--text-secondary)', lineHeight: 'var(--leading-relaxed)' }}>
                  This will remove the demo record from the registry. This action cannot be undone within this session.
                </p>
              </div>
            </div>
            <div className="modal-footer">
              <button className="btn btn-secondary" onClick={() => setMode('view')}>Cancel</button>
              <button className="btn btn-danger" onClick={handleDelete}>
                <Trash2 size={14} aria-hidden="true" /> Confirm Remove
              </button>
            </div>
          </>
        )}
      </div>
    </div>
  );
}
