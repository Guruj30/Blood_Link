// ============================================================
// BLOODLINK — CURSOR INTERACTIONS (restrained, premium)
// Only active on non-touch desktop. Respects reduced-motion.
// Never interferes with clicking, text selection, or forms.
// ============================================================
import { useEffect, useRef } from 'react';

export function useMagneticButton(ref, strength = 0.25) {
  useEffect(() => {
    const el = ref.current;
    if (!el) return;
    if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) return;
    if ('ontouchstart' in window) return;

    let raf = null;
    let tx = 0, ty = 0;

    const onMove = (e) => {
      const rect = el.getBoundingClientRect();
      const cx = rect.left + rect.width / 2;
      const cy = rect.top + rect.height / 2;
      const dx = e.clientX - cx;
      const dy = e.clientY - cy;
      tx = dx * strength;
      ty = dy * strength;
      raf = requestAnimationFrame(() => {
        el.style.transform = `translate(${tx}px, ${ty}px)`;
      });
    };

    const onLeave = () => {
      cancelAnimationFrame(raf);
      raf = requestAnimationFrame(() => {
        el.style.transform = '';
      });
    };

    el.addEventListener('mousemove', onMove, { passive: true });
    el.addEventListener('mouseleave', onLeave, { passive: true });
    return () => {
      el.removeEventListener('mousemove', onMove);
      el.removeEventListener('mouseleave', onLeave);
      cancelAnimationFrame(raf);
      el.style.transform = '';
    };
  }, [ref, strength]);
}

// Subtle cursor-following highlight for selected panels
export function useCursorGlow(ref, color = 'rgba(121, 61, 74, 0.06)') {
  useEffect(() => {
    const el = ref.current;
    if (!el) return;
    if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) return;
    if ('ontouchstart' in window) return;

    let raf = null;

    const onMove = (e) => {
      const rect = el.getBoundingClientRect();
      const x = e.clientX - rect.left;
      const y = e.clientY - rect.top;
      cancelAnimationFrame(raf);
      raf = requestAnimationFrame(() => {
        el.style.backgroundImage =
          `radial-gradient(300px circle at ${x}px ${y}px, ${color}, transparent 70%)`;
      });
    };

    const onLeave = () => {
      cancelAnimationFrame(raf);
      el.style.backgroundImage = '';
    };

    el.addEventListener('mousemove', onMove, { passive: true });
    el.addEventListener('mouseleave', onLeave, { passive: true });
    return () => {
      el.removeEventListener('mousemove', onMove);
      el.removeEventListener('mouseleave', onLeave);
      cancelAnimationFrame(raf);
    };
  }, [ref, color]);
}
