<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Http;
use Webkul\Product\Repositories\ProductRepository;
use Illuminate\Support\Facades\Log;

class ERPProductController extends Controller
{
    /**
     * @param  \Webkul\Product\Repositories\ProductRepository  $productRepository
     */
    public function __construct(
        protected ProductRepository $productRepository
    ) {
    }

    /**
     * Handle the event from ERP (Spring Boot)
     * 
     * ERP sends: { "event": "product.created", "erp_id": 123 }
     */
    public function syncFromERP(Request $request)
    {
        // SECURITY: Verify the ERP Secret Key
        $secretKey = $request->header('X-ERP-KEY');
        if ($secretKey !== config('app.erp_secret_key', 'your-super-secret-key')) {
            return response()->json(['error' => 'Unauthorized: Invalid ERP Key'], 401);
        }

        $erpId = $request->input('erp_id');

        try {
            // 1. FETCH REAL DATA from Spring Boot
            $response = Http::get("http://localhost:8080/erp/products/" . $erpId);

            if ($response->failed()) {
                return response()->json(['error' => 'Could not fetch data from Spring Boot ERP'], 400);
            }

            $erpData = $response->json();



            // 2. Prepare data for Bagisto ProductRepository
            // NOTE: Bagisto requires specific EAV attributes.
            $data = [
                'type' => 'simple', // simple, configurable, virtual, etc.
                'attribute_family_id' => 1,          // Default family (usually 1)
                'sku' => $erpData['sku'] ?? 'ERP-' . $erpId,
                'name' => $erpData['name'],
                'url_key' => strtolower(trim(preg_replace('/[^A-Za-z0-9-]+/', '-', $erpData['name']))),
                'price' => $erpData['price'],
                'weight' => $erpData['weight'] ?? 0,
                'status' => 1,
                'visible_individually' => 1,
                'featured' => 1,
                'short_description' => $erpData['name'],
                'description' => $erpData['description'] ?? $erpData['name'],
                'inventory_sources' => [
                    1 => $erpData['quantity'] ?? 0
                ],
                'channel' => core()->getCurrentChannelCode(),
                'locale' => app()->getLocale(),
                'channels' => [core()->getCurrentChannel()->id],
                'categories' => [1],
            ];

            // 3. Create or Update the product
            $product = $this->productRepository->findOneByField('sku', $data['sku']);

            if ($product) {
                // UPDATE existing product
                $this->productRepository->update($data, $product->id);
                $message = "Product updated successfully";
            } else {
                // CREATE base product first
                $product = $this->productRepository->create($data);

                // UPDATE it immediately to save EAV attributes (name, price, status, etc.)
                $product = $this->productRepository->update($data, $product->id);

                $message = "Product created successfully";
            }

            return response()->json([
                'status' => 'success',
                'message' => $message,
                'product_id' => $product->id
            ]);

        } catch (\Exception $e) {
            Log::error("ERP Sync Failed: " . $e->getMessage());
            return response()->json(['error' => $e->getMessage()], 500);
        }
    }
}
