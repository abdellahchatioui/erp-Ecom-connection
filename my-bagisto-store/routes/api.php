<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\ERPProductController;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
*/

Route::get('/test-connection', function () {
    return response()->json([
        'status'  => 'success',
        'message' => 'Hello World! Bagisto API is reachable.',
        'time'    => now()->toDateTimeString(),
    ]);
});

// ERP Sync Route
Route::post('/erp/sync-product', [ERPProductController::class, 'syncFromERP']);