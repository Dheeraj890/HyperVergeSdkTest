 // Sort by date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        planUnitsBySubFunds.sort(Comparator.comparing(d -> {
            try {
                return sdf.parse(d.getString("date"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));

        // Track latest known balanceUnits
        Map<String, Double> latestBalances = new LinkedHashMap<>();

        // Update each entry in-place
        for (Document entry : planUnitsBySubFunds) {
            String date = entry.getString("date");
            List<Document> currentUnits = (List<Document>) entry.get("unitsBySubFundId");

            // Create a map for current date's subFundId -> balanceUnits
            Map<String, Double> currentMap = new HashMap<>();
            for (Document unit : currentUnits) {
                String subFundId = unit.getString("subFundId");
                Double balance = unit.getDouble("balanceUnits");
                currentMap.put(subFundId, balance);
                latestBalances.put(subFundId, balance); // update latest known
            }

            // Build updated list using all known subFundIds up to this point
            List<Document> updatedUnits = new ArrayList<>();
            for (String subFundId : latestBalances.keySet()) {
                Double balance = currentMap.getOrDefault(subFundId, latestBalances.get(subFundId));
                updatedUnits.add(new Document("subFundId", subFundId).append("balanceUnits", balance));
            }

            // Replace unitsBySubFundId with updated version
            entry.put("unitsBySubFundId", updatedUnits);
        }

        // Print final result
        for (Document d : planUnitsBySubFunds) {
            System.out.println(d.toJson());
        }
