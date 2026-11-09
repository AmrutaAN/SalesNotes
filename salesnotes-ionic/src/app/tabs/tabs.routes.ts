import type { Routes } from '@angular/router';
import { TabsPage } from './tabs.page';

export const routes: Routes = [
  {
    path: 'tabs',
    component: TabsPage,
    children: [
      {
        path: 'dashboard',
        loadComponent: () => import('../pages/dashboard/dashboard.page').then((m) => m.DashboardIonicComponent),
      },
      {
        path: 'notes',
        loadComponent: () => import('../pages/note-list/note-list.page').then((m) => m.NoteListIonicComponent),
      },
      {
        path: 'notes/new',
        loadComponent: () => import('../pages/note-form/note-form.page').then((m) => m.NoteFormIonicComponent),
      },
      {
        path: 'notes/:id',
        loadComponent: () => import('../pages/note-detail/note-detail.page').then((m) => m.NoteDetailIonicComponent),
      },
      {
        path: 'notes/:id/edit',
        loadComponent: () => import('../pages/note-form/note-form.page').then((m) => m.NoteFormIonicComponent),
      },
      {
        path: 'customers',
        loadComponent: () => import('../pages/customer-list/customer-list.page').then((m) => m.CustomerListIonicComponent),
      },
      {
        path: '',
        redirectTo: '/tabs/dashboard',
        pathMatch: 'full',
      },
    ],
  },
  {
    path: '',
    redirectTo: '/tabs/dashboard',
    pathMatch: 'full',
  },
];
