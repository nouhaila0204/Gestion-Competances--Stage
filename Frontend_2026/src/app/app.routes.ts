import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  // ✅ PAGE D'ACCUEIL (Landing) en premier
  {
    path: '',
    loadComponent: () => import('./features/landing/landing').then((m) => m.Landing),
  },
  {
    path: 'candidature',
    loadComponent: () => import('./features/candidature/candidature').then((m) => m.Candidature),
  },

  // ✅ LOGIN
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login').then((m) => m.Login),
  },

  // ✅ STAGIAIRE
  {
    path: 'stagiaire',
    canActivate: [authGuard, roleGuard],
    data: {
      roles: ['STAGIAIRE'],
      navItems: [
        { label: '📋 Ma demande', route: '/stagiaire/dashboard' },
        { label: '➕ Nouvelle demande', route: '/stagiaire/nouvelle' },
        { label: '📜 Historique', route: '/stagiaire/historique' },
      ],
    },
    loadComponent: () => import('./shared/layout/layout').then((m) => m.Layout),
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/stagiaire/dashboard/dashboard').then((m) => m.Dashboard),
      },
      {
        path: 'nouvelle',
        loadComponent: () =>
          import('./features/stagiaire/ma-demande/ma-demande').then((m) => m.MaDemande),
      },
      {
        path: 'historique',
        loadComponent: () =>
          import('./features/stagiaire/historique/historique').then((m) => m.Historique),
      },
      {
        path: 'profil',
        loadComponent: () => import('./features/stagiaire/profil/profil').then((m) => m.Profil),
      },
    ],
  },

  // ✅ RESPONSABLE STAGE
  {
    path: 'responsable-stage',
    canActivate: [authGuard, roleGuard],
    data: {
      roles: ['RESPONSABLE_STAGE'],
      navItems: [
        { label: 'Dashboard', route: '/responsable-stage/dashboard' },
        { label: 'Demandes', route: '/responsable-stage/demandes' },
        { label: 'Stages', route: '/responsable-stage/stages' },
      ],
    },
    loadComponent: () => import('./shared/layout/layout').then((m) => m.Layout),
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/responsable-stage/dashboard/dashboard').then((m) => m.Dashboard),
      },
      {
        path: 'demandes',
        loadComponent: () =>
          import('./features/responsable-stage/demandes/demandes').then((m) => m.Demandes),
      },
      {
        path: 'stages',
        loadComponent: () =>
          import('./features/responsable-stage/stages/stages').then((m) => m.Stages),
      },
      {
        path: 'profil',
        loadComponent: () => import('./features/stagiaire/profil/profil').then((m) => m.Profil),
      }, // ✅ AJOUTER
    ],
  },

  // ✅ DIRECTEUR RH
  {
    path: 'directeur-rh',
    canActivate: [authGuard, roleGuard],
    data: {
      roles: ['DIRECTEUR_RH'],
      navItems: [
        { label: 'Demandes', route: '/directeur-rh/demandes' },
        { label: 'Offres', route: '/directeur-rh/offres' },
      ],
    },
    loadComponent: () => import('./shared/layout/layout').then((m) => m.Layout),
    children: [
      { path: '', redirectTo: 'demandes', pathMatch: 'full' },
      {
        path: 'demandes',
        loadComponent: () =>
          import('./features/directeur-rh/demandes/demandes').then((m) => m.Demandes),
      },
      {
        path: 'offres',
        loadComponent: () => import('./features/directeur-rh/offres/offres').then((m) => m.Offres),
      },
      {
        path: 'profil',
        loadComponent: () => import('./features/stagiaire/profil/profil').then((m) => m.Profil),
      }, // ✅ AJOUTER
    ],
  },

  // ✅ RESPONSABLE SERVICE
  {
    path: 'responsable-service',
    canActivate: [authGuard, roleGuard],
    data: {
      roles: ['RESPONSABLE_SERVICE'],
      navItems: [
        { label: 'Dashboard', route: '/responsable-service/dashboard' },
        { label: 'Mes stagiaires', route: '/responsable-service/stagiaires' },
        { label: 'Évaluations', route: '/responsable-service/evaluations' },
      ],
    },
    loadComponent: () => import('./shared/layout/layout').then((m) => m.Layout),
    children: [
      { path: '', redirectTo: 'Dashboard', pathMatch: 'full' },
      {
        path: 'stagiaires',
        loadComponent: () =>
          import('./features/responsable-service/stagiaires/stagiaires').then((m) => m.Stagiaires),
      },
      {
        path: 'profil',
        loadComponent: () => import('./features/stagiaire/profil/profil').then((m) => m.Profil),
      },
      {
        path: 'evaluations',
        loadComponent: () =>
          import('./features/responsable-service/evaluations/evaluation').then((m) => m.Evaluation),
      },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/responsable-service/dashboard/dashboard').then((m) => m.Dashboard),
      },
    ],
  },

  // ✅ ACCÈS REFUSÉ
  {
    path: 'acces-refuse',
    loadComponent: () => import('./shared/acces-refuse/acces-refuse').then((m) => m.AccesRefuse),
  },

  // ✅ REDIRECTION 404
  { path: '**', redirectTo: '' },
];
