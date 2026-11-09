export type Priority = 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';
export type NoteStatus = 'OPEN' | 'IN_PROGRESS' | 'FOLLOW_UP_REQUIRED' | 'WON' | 'LOST' | 'CLOSED';

export interface Customer {
  id?: number;
  name: string;
  company: string;
  email?: string;
  phone?: string;
  industry?: string;
  createdAt?: string;
}

export interface NoteCategory {
  id?: number;
  name: string;
  color?: string;
}

export interface SalesNote {
  id?: number;
  title: string;
  content: string;
  customer: Customer;
  category?: NoteCategory;
  dealAmount?: number;
  priority: Priority;
  status: NoteStatus;
  followUpDate?: string;
  tags?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface SalesNoteRequest {
  title: string;
  content?: string;
  customerId: number;
  categoryId?: number;
  dealAmount?: number;
  priority?: Priority;
  status?: NoteStatus;
  followUpDate?: string;
  tags?: string;
}

export interface DashboardSummary {
  totalNotes: number;
  openNotes: number;
  wonNotes: number;
  urgentNotes: number;
  totalPipelineValue: number;
  recentNotes: SalesNote[];
}
