export type InteractionType = 'field_visit' | 'phone_call' | 'video_call' | 'post_visit_note';
export type TagCode = 'ORDER' | 'COMPLAINT' | 'INFO' | 'FOLLOWUP' | 'PRICE' | string;
export type ExportTargetModule = 'sales_order' | 'complaint' | 'lead' | 'other';
export type TranscriptionStatus = 'pending' | 'processing' | 'awaiting_confirmation' | 'confirmed' | 'discarded';

export interface ClientProfile {
  id: string;
  name: string;
  company?: string;
  phone?: string;
  email?: string;
  address?: string;
  industryTag?: string;
  priorityFlag: boolean;
  lastInteractionAt?: string;
  notesCount: number;
  clientNoteId?: string;
  assignedRepName?: string;
}

export interface ClientSummary {
  clientId: string;
  clientName: string;
  company?: string;
  totalNotes: number;
  openOrdersCount: number;
  openComplaintsCount: number;
  pendingFollowupsCount: number;
  priorityFlag: boolean;
  lastInteractionAt?: string;
}

export interface TagInfo {
  id: string;
  code: TagCode;
  label: string;
  colorHex: string;
  description?: string;
  isSystem: boolean;
  isActive: boolean;
}

export interface AttachmentInfo {
  id: string;
  attachmentType: 'document' | 'image' | 'link' | 'voice_note';
  fileName?: string;
  publicUrl?: string;
  linkUrl?: string;
  linkTitle?: string;
  mimeType?: string;
  fileSizeBytes?: number;
  durationSeconds?: number;
  createdAt: string;
}

export interface LogEntryItem {
  id: string;
  clientNoteId: string;
  interactionType: InteractionType;
  interactionDate: string;
  interactionTime?: string;
  textContent: string;
  priority: boolean;
  hasAttachments: boolean;
  hasVoiceNote: boolean;
  hasTranscription: boolean;
  tag?: TagInfo;
  attachments?: AttachmentInfo[];
  createdByName?: string;
  createdAt: string;
  updatedAt?: string;
}

export interface CataloguePriceTier {
  id?: string;
  minQuantity: number;
  maxQuantity?: number;
  discountPercentage: number;
  tierName?: string;
}

export interface CatalogueProduct {
  id: string;
  sku: string;
  name: string;
  category: string;
  uom: string;
  basePrice: number;
  defaultDiscountPct: number;
  netPrice: number;
  taxRatePct: number;
  hasTieredPricing: boolean;
  isActive: boolean;
  priceTiers?: CataloguePriceTier[];
}

export interface PriceQuoteRequest {
  itemId: string;
  quantity: number;
}

export interface PriceQuoteResult {
  itemId: string;
  sku: string;
  itemName: string;
  quantity: number;
  basePrice: number;
  applicableDiscountPct: number;
  tierApplied?: string;
  unitPriceAfterDiscount: number;
  netPrice: number;
  taxRatePct: number;
  taxAmount: number;
  finalTotalPrice: number;
}

export interface ClipboardEntryItem {
  id: string;
  stagedAt: string;
  logEntryId: string;
  interactionType: InteractionType;
  interactionDate: string;
  textContent: string;
  priority: boolean;
  clientNoteId: string;
  clientId: string;
  clientName: string;
  clientCompany?: string;
  tagCode?: string;
  tagName?: string;
  tagColor?: string;
  deepLinkUrl?: string;
}

export interface ClipboardSessionData {
  id: string;
  userId: string;
  status: 'active' | 'cleared' | 'exported';
  entryCount: number;
  expiresAt?: string;
  clearedAt?: string;
  updatedAt?: string;
  entries: ClipboardEntryItem[];
}

export interface ExportRecordItem {
  id: string;
  clipboardSessionId: string;
  exportedById: string;
  exportedByName?: string;
  targetModule: ExportTargetModule;
  externalRefId?: string;
  entryCount: number;
  status: string;
  exportedAt: string;
  exportPayload: string;
}

export interface VoiceTranscriptionRecord {
  id: string;
  status: TranscriptionStatus;
  rawTranscript?: string;
  editedTranscript?: string;
  finalTranscript?: string;
  sourceLanguageHint?: string;
  aiModelUsed?: string;
  logEntryAttachmentId?: string;
  logEntryId?: string;
  processingStartedAt?: string;
  processingCompletedAt?: string;
  confirmedAt?: string;
  discardedAt?: string;
  createdAt: string;
}
